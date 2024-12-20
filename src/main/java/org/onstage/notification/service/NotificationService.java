package org.onstage.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.device.service.DeviceService;
import org.onstage.enums.NotificationActionStatus;
import org.onstage.enums.NotificationStatus;
import org.onstage.enums.NotificationType;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.model.Notification;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.model.PaginatedNotifications;
import org.onstage.notification.repository.NotificationRepository;
import org.onstage.socketio.service.SocketIOService;
import org.onstage.usersettings.model.UserSettings;
import org.onstage.usersettings.service.UserSettingsService;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.onstage.enums.NotificationType.EVENT_INVITATION_REQUEST;
import static org.onstage.enums.NotificationType.TEAM_INVITATION_REQUEST;
import static org.onstage.socketio.SocketEventType.NOTIFICATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    public final static List<NotificationType> ACTION_NOTIFICATIONS = List.of(EVENT_INVITATION_REQUEST, TEAM_INVITATION_REQUEST);
    private final NotificationRepository notificationRepository;
    private final SocketIOService socketIOService;
    private final DeviceService deviceService;
    private final PushNotificationService pushNotificationService;
    private final UserSettingsService userSettingsService;

    public PaginatedNotifications getNotificationsForUser(String userId, String currentTeamId, int offset, int limit) {
        return notificationRepository.findNotifications(userId, currentTeamId, offset, limit);
    }

    public void sendNotificationToUser(NotificationType type, String userToNotify, String description, String title, String teamId, NotificationParams params) {
        params.setTeamId(teamId);
        Notification notification = Notification.builder()
                .type(type)
                .status(NotificationStatus.NEW)
                .actionStatus(ACTION_NOTIFICATIONS.contains(type) ? NotificationActionStatus.PENDING : NotificationActionStatus.NONE)
                .userToNotify(userToNotify)
                .params(params)
                .title(title)
                .description(description)
                .build();
        notificationRepository.save(notification);

        deviceService.getAllLoggedDevices(userToNotify).forEach(device -> {
            socketIOService.sendSocketEvent(notification.getUserToNotify(), device.getDeviceId(), NOTIFICATION, null);
            UserSettings userSettings = userSettingsService.getUserSettings(notification.getUserToNotify());
            if (userSettings != null && userSettings.getIsNotificationsEnabled()) {
                pushNotificationService.sendPushNotification(title, description, device.getPushToken());
            }
        });
        log.info("New notification {} has been sent to user {}", type, notification.getUserToNotify());
    }

    public void markAllNotificationsAsViewed(String userId) {
        notificationRepository.markAllNotificationsAsViewed(userId);

    }

    public void updateNotification(String id, Notification request) {
        Notification existingNotification = getById(id);
        existingNotification.setActionStatus(request.getActionStatus() != null ? request.getActionStatus() : existingNotification.getActionStatus());
        notificationRepository.save(existingNotification);
    }

    public Notification getById(String id) {
        return notificationRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("notification"));
    }

    public void deleteNotificationByTeamId(NotificationType notificationType, String teamId, String userId) {
        notificationRepository.deleteNotificationByTeamId(notificationType, teamId, userId);
    }

    public void deleteNotificationByEventId(NotificationType notificationType, String eventId, String userId) {
        notificationRepository.deleteNotificationByEventId(notificationType, eventId, userId);
    }
}

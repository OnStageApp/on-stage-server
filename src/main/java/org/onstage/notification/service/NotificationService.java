package org.onstage.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.device.service.DeviceService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.action.GetNotificationsAction;
import org.onstage.notification.client.*;
import org.onstage.notification.model.Notification;
import org.onstage.notification.repository.NotificationRepository;
import org.onstage.socketio.service.SocketIOService;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.onstage.socketio.SocketEventType.NOTIFICATION;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final GetNotificationsAction getNotificationsAction;
    private final NotificationRepository notificationRepository;
    private final SocketIOService socketIOService;
    private final DeviceService deviceService;
    private final PushNotificationService pushNotificationService;
    private final UserSecurityContext userSecurityContext;

    public List<Notification> getAllNotifications(NotificationFilter filter) {
        return getNotificationsAction.execute(filter);
    }

    public void sendNotificationToUser(NotificationType type, String userId, String description, String title, String eventId) {
        NotificationActionStatus actionStatus = type == NotificationType.EVENT_INVITATION_REQUEST || type == NotificationType.TEAM_INVITATION_REQUEST ? NotificationActionStatus.PENDING : NotificationActionStatus.NONE;
        Notification notification = Notification.builder()
                .type(type)
                .status(NotificationStatus.NEW)
                .actionStatus(actionStatus)
                .userId(userId)
                .eventId(eventId)
                .description(description)
                .build();
        notificationRepository.save(notification);

        deviceService.getAllLoggedDevices(userId).forEach(device -> {
            socketIOService.sendSocketEvent(notification.getUserId(), device.getDeviceId(), NOTIFICATION, null);
            pushNotificationService.sendPushNotification(title, description, device.getPushToken());

        });
        log.info("New notification {} has been sent to user {}", type, notification.getUserId());
    }

    public void markAllNotificationsAsViewed() {
        final String userId = userSecurityContext.getUserId();
        notificationRepository.markAllNotificationsAsViewed(userId);

    }

    public void updateNotification(Notification existingNotification, NotificationDTO notificationDTO) {

        Notification notification =
                existingNotification
                        .withActionStatus(notificationDTO.actionStatus());
        notificationRepository.save(notification);
    }

    public Notification getById(String id) {
        return notificationRepository.findById(id);
    }
}

package org.onstage.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.notification.action.GetNotificationsAction;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.client.NotificationStatus;
import org.onstage.notification.client.NotificationType;
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

    public List<Notification> getAllNotifications(NotificationFilter filter) {
        return getNotificationsAction.execute(filter);
    }

    public void sendNotificationToUser(NotificationType type, NotificationStatus status, String userId, String description) {
        Notification notification = Notification.builder()
                .type(type)
                .status(status)
                .userId(userId)
                .description(description)
                .build();
        notificationRepository.save(notification);
        socketIOService.sendToUser(notification.userId(), NOTIFICATION, null);
        log.info("New notification {} has been sent to user {}", type, notification.userId());
    }
}

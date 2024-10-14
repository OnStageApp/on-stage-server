package org.onstage.notification.service;

import lombok.RequiredArgsConstructor;
import org.onstage.notification.action.GetNotificationsAction;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.NotificationEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationService {
    private final GetNotificationsAction getNotificationsAction;

    public List<NotificationEntity> getAllNotifications(NotificationFilter filter) {
        return getNotificationsAction.execute(filter);
    }
}

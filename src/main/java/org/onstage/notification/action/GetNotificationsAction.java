package org.onstage.notification.action;

import lombok.RequiredArgsConstructor;
import org.onstage.common.action.Action;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.NotificationEntity;
import org.onstage.notification.repository.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetNotificationsAction implements Action<NotificationFilter, List<NotificationEntity>> {
    private final NotificationRepository repository;

    @Override
    public List<NotificationEntity> doExecute(NotificationFilter filter) {
        return repository.findNotifications(filter);
    }
}

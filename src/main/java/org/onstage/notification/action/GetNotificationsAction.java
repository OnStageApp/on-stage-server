package org.onstage.notification.action;

import lombok.RequiredArgsConstructor;
import org.onstage.common.action.Action;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.Notification;
import org.onstage.notification.repository.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
//@RequiredArgsConstructor
//public class GetNotificationsAction implements Action<NotificationFilter, List<Notification>> {
//    private final NotificationRepository repository;
//
//    @Override
//    public List<Notification> doExecute(NotificationFilter filter) {
//        return repository.findNotifications(filter);
//    }
//}

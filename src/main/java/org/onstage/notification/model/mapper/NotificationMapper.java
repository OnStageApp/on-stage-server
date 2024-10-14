package org.onstage.notification.model.mapper;

import org.onstage.common.mappers.GenericMapper;
import org.onstage.notification.client.Notification;
import org.onstage.notification.model.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements GenericMapper<NotificationEntity, Notification> {

    @Override
    public NotificationEntity toDb(Notification source) {
        return NotificationEntity.builder()
                .description(source.description())
                .status(source.status())
                .type(source.type())
                .userId(source.userId())
                .build();
    }

    @Override
    public Notification toApi(NotificationEntity source) {
        return Notification.builder()
                .notificationId(source.notificationId())
                .description(source.description())
                .status(source.status())
                .type(source.type())
                .userId(source.userId())
                .build();
    }
}

package org.onstage.notification.model.mapper;

import org.onstage.common.mappers.GenericMapper;
import org.onstage.notification.client.NotificationDTO;
import org.onstage.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements GenericMapper<Notification, NotificationDTO> {

    @Override
    public Notification toDb(NotificationDTO source) {
        return Notification.builder()
                .description(source.description())
                .status(source.status())
                .type(source.type())
                .userId(source.userId())
                .build();
    }

    @Override
    public NotificationDTO toApi(Notification source) {
        return NotificationDTO.builder()
                .notificationId(source.notificationId())
                .description(source.description())
                .status(source.status())
                .type(source.type())
                .userId(source.userId())
                .build();
    }
}

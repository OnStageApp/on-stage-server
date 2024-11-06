package org.onstage.notification.model.mapper;

import org.onstage.common.mappers.GenericMapper;
import org.onstage.notification.client.NotificationDTO;
import org.onstage.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper implements GenericMapper<Notification, NotificationDTO> {

    @Override
    public Notification toEntity(NotificationDTO source) {
        return Notification.builder()
                .notificationId(source.notificationId())
                .title(source.title())
                .description(source.description())
                .actionStatus(source.actionStatus())
                .params(source.params())
                .type(source.type())
                .status(source.status())
                .userToNotify(source.userToNotify())
                .build();
    }

    @Override
    public NotificationDTO toDTO(Notification source) {
        return NotificationDTO.builder()
                .notificationId(source.getNotificationId())
                .title(source.getTitle())
                .description(source.getDescription())
                .actionStatus(source.getActionStatus())
                .params(source.getParams())
                .type(source.getType())
                .status(source.getStatus())
                .userToNotify(source.getUserToNotify())
                .build();
    }
}

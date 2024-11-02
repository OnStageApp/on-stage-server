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
                .actionStatus(source.actionStatus())
                .type(source.type())
                .eventId(source.eventId())
                .stagerId(source.stagerId())
                .userId(source.userId())
                .build();
    }

    @Override
    public NotificationDTO toApi(Notification source) {
        return NotificationDTO.builder()
                .notificationId(source.getNotificationId())
                .description(source.getDescription())
                .status(source.getStatus())
                .actionStatus(source.getActionStatus())
                .type(source.getType())
                .eventId(source.getEventId())
                .stagerId(source.getStagerId())
                .userId(source.getUserId())
                .build();
    }
}

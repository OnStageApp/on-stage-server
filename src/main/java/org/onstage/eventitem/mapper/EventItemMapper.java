package org.onstage.eventitem.mapper;

import org.onstage.eventitem.client.CreateEventItemRequest;
import org.onstage.eventitem.client.EventItem;
import org.onstage.eventitem.model.EventItemEntity;
import org.springframework.stereotype.Component;

@Component
public class EventItemMapper {
    public EventItem toDto(EventItemEntity entity) {
        return EventItem.builder()
                .id(entity.id())
                .name(entity.name())
                .index(entity.index())
                .eventType(entity.eventType())
//                .songId(entity.songId())
                .eventId(entity.eventId())
                .build();
    }

    public EventItemEntity fromCreateRequest(CreateEventItemRequest eventItem) {
        return EventItemEntity.builder()
                .name(eventItem.name())
                .index(eventItem.index())
                .eventType(eventItem.eventType())
                .songId(eventItem.songId())
                .eventId(eventItem.eventId())
                .build();
    }
}
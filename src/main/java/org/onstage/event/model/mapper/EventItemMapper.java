package org.onstage.event.model.mapper;

import org.onstage.event.client.EventItem;
import org.onstage.event.model.EventItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventItemMapper {
    public EventItem toApi(EventItemEntity entity) {
        return EventItem.builder()
                .id(entity.id())
                .name(entity.name())
                .index(entity.index())
                .eventType(entity.eventType())
                .songId(entity.songId())
                .eventId(entity.eventId())
                .build();
    }

    public EventItemEntity toDb(EventItem request) {
        return EventItemEntity.builder()
                .name(request.name())
                .index(request.index())
                .eventType(request.eventType())
                .songId(request.songId())
                .eventId(request.eventId())
                .build();
    }

    public List<EventItem> toApiList(List<EventItemEntity> entities) {
        return entities.stream()
                .map(this::toApi)
                .toList();
    }

    public List<EventItemEntity> toDbList(List<EventItem> requests) {
        return requests.stream()
                .map(this::toDb)
                .toList();
    }
}
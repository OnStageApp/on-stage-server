package org.onstage.eventitem.mapper;

import org.onstage.enums.EventItemType;
import org.onstage.eventitem.client.CreateEventItem;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.model.EventItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventItemMapper {
    public EventItemDTO toDto(EventItem entity) {
        return EventItemDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .index(entity.getIndex())
                .eventType(entity.getEventType())
                .eventId(entity.getEventId())
                .build();
    }


    public EventItem fromCreateRequest(CreateEventItem eventItem, String eventId) {
        return EventItem.builder()
                .name(eventItem.name())
                .index(eventItem.index())
                .eventType(eventItem.songId() != null ? EventItemType.SONG : EventItemType.OTHER)
                .songId(eventItem.songId())
                .eventId(eventId)
                .build();
    }

    public List<EventItem> fromCreateRequestList(List<CreateEventItem> eventItems, String eventId) {
        return eventItems.stream()
                .map(item -> fromCreateRequest(item, eventId))
                .toList();
    }

    public EventItem toEntity(EventItemDTO request) {
        return EventItem.builder()
                .id(request.id())
                .name(request.name())
                .index(request.index())
                .eventType(request.eventType())
                .eventId(request.eventId())
                .build();
    }
}
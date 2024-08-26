package org.onstage.eventitem.mapper;

import org.onstage.eventitem.client.CreateEventItem;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.model.EventItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventItemMapper {
    public EventItemDTO toDto(EventItem entity) {
        return EventItemDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .index(entity.index())
                .eventType(entity.eventType())
                .eventId(entity.eventId())
                .build();
    }


    public EventItem fromCreateRequest(CreateEventItem eventItem) {
        return EventItem.builder()
                .name(eventItem.name())
                .index(eventItem.index())
                .eventType(eventItem.eventType())
                .songId(eventItem.songId())
                .eventId(eventItem.eventId())
                .build();
    }

    public List<EventItem> fromCreateRequestList(List<CreateEventItem> eventItems) {
        return eventItems.stream()
                .map(this::fromCreateRequest)
                .toList();
    }
}
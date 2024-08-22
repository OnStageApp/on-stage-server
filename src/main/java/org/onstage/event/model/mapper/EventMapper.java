package org.onstage.event.model.mapper;

import org.onstage.event.client.CreateEventRequest;
import org.onstage.event.client.Event;
import org.onstage.event.model.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toDto(EventEntity entity) {
        return Event.builder()
                .id(entity.id())
                .name(entity.name())
                .date(entity.dateTime())
                .location(entity.location())
                .eventStatus(entity.eventStatus())
                .build();
    }

    public EventEntity fromCreateRequest(CreateEventRequest request) {
        return EventEntity.builder()
                .name(request.name())
                .dateTime(request.dateTime())
                .location(request.location())
                .eventStatus(request.eventStatus())
                .build();
    }
}
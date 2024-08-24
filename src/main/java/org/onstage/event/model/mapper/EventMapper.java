package org.onstage.event.model.mapper;

import org.onstage.event.client.CreateEventRequest;
import org.onstage.event.client.EventDTO;
import org.onstage.event.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public EventDTO toDto(Event entity) {
        return EventDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .date(entity.dateTime())
                .location(entity.location())
                .eventStatus(entity.eventStatus())
                .build();
    }

    public Event fromCreateRequest(CreateEventRequest request) {
        return Event.builder()
                .name(request.name())
                .dateTime(request.dateTime())
                .location(request.location())
                .eventStatus(request.eventStatus())
                .build();
    }
}

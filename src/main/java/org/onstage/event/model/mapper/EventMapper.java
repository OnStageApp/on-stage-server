package org.onstage.event.model.mapper;

import org.onstage.event.client.Event;
import org.onstage.event.client.EventOverview;
import org.onstage.event.model.EventEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {
    public Event toDto(EventEntity entity) {
        return Event.builder()
                .id(entity.id())
                .name(entity.name())
                .date(entity.date())
                .location(entity.location())
                .eventStatus(entity.eventStatus())
                .build();
    }

    public EventEntity toEntity(Event request) {
        return EventEntity.builder()
                .id(request.id())
                .name(request.name())
                .date(request.date())
                .location(request.location())
                .eventStatus(request.eventStatus())
                .build();
    }

    public List<Event> toDtoList(List<EventEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public List<EventEntity> toEntityList(List<Event> requests) {
        return requests.stream()
                .map(this::toEntity)
                .toList();
    }

    public List<EventOverview> toOverviewList(List<EventEntity> entities) {
        return entities.stream()
                .map(this::toOverview)
                .toList();
    }

    public EventOverview toOverview(EventEntity entity) {
        return EventOverview.builder()
                .id(entity.id())
                .name(entity.name())
                .date(entity.date())
                .build();
    }
}

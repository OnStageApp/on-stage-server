package org.onstage.event.model.mapper;

import org.onstage.event.client.CreateEventRequest;
import org.onstage.event.client.Event;
import org.onstage.event.client.EventOverview;
import org.onstage.event.client.IEvent;
import org.onstage.event.model.EventEntity;
import org.onstage.stager.client.IStagerOverview;
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

    public EventEntity fromCreateRequest(CreateEventRequest request) {
        return EventEntity.builder()
                .name(request.name())
                .date(request.dateTime())
                .location(request.location())
                .eventStatus(request.eventStatus())
                .build();
    }

    public List<EventOverview> toOverviewList(List<IEvent> projection) {
        return projection.stream()
                .map(this::toOverview)
                .toList();
    }

    public EventOverview toOverview(IEvent projection) {
        return EventOverview.builder()
                .id(projection.getId())
                .name(projection.getName())
                .date(projection.getDate())
                .stagersPhotos(projection.getStagers().stream().map(IStagerOverview::getPhoto).toList())
                .build();
    }
}

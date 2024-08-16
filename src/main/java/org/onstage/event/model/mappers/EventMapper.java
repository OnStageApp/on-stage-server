package org.onstage.event.model.mappers;

import org.mapstruct.Mapper;
import org.onstage.event.client.Event;
import org.onstage.event.client.EventOverview;
import org.onstage.event.model.EventEntity;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventItemMapper.class})
public interface EventMapper {
    Event toDto(EventEntity entity);

    EventEntity toEntity(Event request);

    List<Event> toDtoList(List<EventEntity> entities);

    List<EventEntity> toEntityList(List<Event> requests);

    List<EventOverview> toOverviewList(List<EventEntity> entities);

    EventOverview toOverview(EventEntity entity);
}

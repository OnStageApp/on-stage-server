package org.onstage.event.model.mappers;

import org.mapstruct.Mapper;
import org.onstage.event.client.Event;
import org.onstage.event.model.EventEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toApi(EventEntity entity);

    EventEntity toDb(Event request);

    List<Event> toApiList(List<EventEntity> entities);

    List<EventEntity> toDbList(List<Event> requests);
}

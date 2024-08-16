package org.onstage.event.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.onstage.event.client.EventItem;
import org.onstage.event.model.EventItemEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventItemMapper {
    EventItem toApi(EventItemEntity entity);

    @Mapping(target = "id", ignore = true)
    EventItemEntity toDb(EventItem request);

    List<EventItem> toApiList(List<EventItemEntity> entities);

    List<EventItemEntity> toDbList(List<EventItem> requests);
}

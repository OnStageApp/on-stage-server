package org.onstage.event.model.mappers;

import org.onstage.event.client.Event;
import org.onstage.event.model.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toApi(EventEntity entity) {
        return Event.builder()
                .id(entity.id())
                .build();
    }

    public EventEntity toDb(Event request) {
        return EventEntity.builder()
                .title(request.title())
                .date(request.date())
                .build();
    }
}

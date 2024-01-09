package org.onstage.event.model;


import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@Document("events")
@FieldNameConstants
public record EventEntity(
        String id,
        String name,
        LocalDateTime date,
        List<LocalDateTime> rehearsalDates,
        String location,
        List<String> planners,
        List<EventItemEntity> eventItems,
        List<String> stagerIds
) {
    public EventEntity {
        Objects.requireNonNull(name);
    }
}

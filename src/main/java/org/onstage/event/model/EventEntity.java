package org.onstage.event.model;


import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Document("events")
@FieldNameConstants
public record EventEntity(
        String id,
        String title,
        LocalDateTime date,
        LocalDateTime rehearsalDate,
        String location,
        List<String> planners,
        List<String> eventItemIds,
        List<String> stagerIds
) {
}

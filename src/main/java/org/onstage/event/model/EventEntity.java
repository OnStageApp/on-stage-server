package org.onstage.event.model;


import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Document("events")
@FieldNameConstants
public record EventEntity(
        @MongoId
        String id,
        @NonNull
        String name,
        LocalDateTime date,
        List<LocalDateTime> rehearsalDates,
        String location,
        List<String> planners,
        List<String> stagerIds
) {
}

package org.onstage.rehearsal.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Builder
@Document("rehearsals")
@FieldNameConstants
public record Rehearsal(
        @MongoId
        String id,
        String name,
        String location,
        LocalDateTime dateTime,
        String eventId
) {

}

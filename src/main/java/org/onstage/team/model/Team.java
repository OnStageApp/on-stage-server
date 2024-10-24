package org.onstage.team.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("teams")
@Builder(toBuilder = true)
@FieldNameConstants
public record Team(
        @MongoId
        String id,
        String name,
        String leaderId
) {
}

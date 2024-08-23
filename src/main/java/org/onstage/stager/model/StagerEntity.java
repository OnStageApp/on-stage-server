package org.onstage.stager.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.ParticipationStatus;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Document("stagers")
@FieldNameConstants
@CompoundIndex(name = "eventId_userId_unique", def = "{'eventId': 1, 'userId': 1}", unique = true)
public record StagerEntity(
        @MongoId
        String id,
        String eventId,
        String userId,
        String name,
        String profilePicture,
        ParticipationStatus participationStatus) {
}

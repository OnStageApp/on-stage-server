package org.onstage.event.model;

import lombok.Builder;
import org.onstage.event.enums.EventItemType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document("event-items")
@Builder(toBuilder = true)
public record EventItemEntity(
        @MongoId
        String id,
        String name,
        int index,
        EventItemType eventType,
        String songId,
        String eventId
) {
}

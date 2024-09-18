package org.onstage.eventitem.model;

import lombok.Builder;
import org.onstage.enums.EventItemType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("eventItems")
@Builder(toBuilder = true)
public record EventItem(
        @MongoId
        String id,
        String name,
        Integer index,
        EventItemType eventType,
        String songId,
        String eventId
) {
}

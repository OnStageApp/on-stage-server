package org.onstage.event.model;

import lombok.Builder;
import org.onstage.event.enums.EventItemType;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("event-items")
@Builder(toBuilder = true)
public record EventItemEntity(
        String id,
        String name,
        int index,
        EventItemType eventType,
        String songId
) {
}

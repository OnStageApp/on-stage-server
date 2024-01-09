package org.onstage.event.client;

import org.onstage.event.enums.EventItemType;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("event-item")
public record EventItem(
        String name,
        int index,
        EventItemType eventType,
        String songId
) {
}

package org.onstage.event.client;

import org.onstage.event.enums.EventItemType;

public record EventItem(
        String name,
        int index,
        EventItemType eventType,
        String songId,
        String eventId
) {
}

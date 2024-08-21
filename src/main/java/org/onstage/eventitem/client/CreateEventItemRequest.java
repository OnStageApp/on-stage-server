package org.onstage.eventitem.client;

import lombok.Builder;
import org.onstage.enums.EventItemType;

@Builder(toBuilder = true)
public record CreateEventItemRequest(
        String name,
        int index,
        EventItemType eventType,
        String songId,
        String eventId
) {
}

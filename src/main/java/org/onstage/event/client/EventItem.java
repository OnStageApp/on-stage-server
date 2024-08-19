package org.onstage.event.client;

import lombok.Builder;
import org.onstage.event.enums.EventItemType;

@Builder(toBuilder = true)
public record EventItem(
        String id,
        String name,
        int index,
        EventItemType eventType,
        String songId,
        String eventId
) {
}

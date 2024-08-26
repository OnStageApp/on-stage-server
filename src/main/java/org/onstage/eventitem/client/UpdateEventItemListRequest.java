package org.onstage.eventitem.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record UpdateEventItemListRequest(
        List<CreateEventItem> eventItems,
        String eventId
) {
}

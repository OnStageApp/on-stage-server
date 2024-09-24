package org.onstage.event.client;

import lombok.Builder;
import org.onstage.event.model.Event;

import java.util.List;

@Builder(toBuilder = true)
public record PaginatedEventResponse(List<Event> events, boolean hasMore) {
}

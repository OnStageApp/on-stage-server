package org.onstage.event.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record PaginatedEventResponse(List<EventOverview> events, boolean hasMore) {
}

package org.onstage.event.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DuplicateEventRequest(
        LocalDateTime dateTime,
        String name
) {
}

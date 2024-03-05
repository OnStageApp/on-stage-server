package org.onstage.event.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record EventOverview(
        String id,
        String name,
        LocalDateTime date
) {
}

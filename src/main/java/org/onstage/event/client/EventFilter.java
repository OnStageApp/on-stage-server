package org.onstage.event.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventFilter(
        LocalDateTime startDate,
        LocalDateTime endDate,
        String search
) {
}

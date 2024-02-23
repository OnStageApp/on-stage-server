package org.onstage.event.client;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventFilter(
        LocalDateTime startDate,
        LocalDateTime endDate,
        String search
) {
}

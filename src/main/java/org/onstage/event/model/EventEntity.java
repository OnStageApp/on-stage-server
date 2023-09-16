package org.onstage.event.model;


import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventEntity(
        String id,
        String title,
        LocalDateTime date,
        LocalDateTime rehearsalDate,
        String location,
        List<String> planners,
        List<String> eventItemIds,
        List<String> stagerIds
) {
}

package org.onstage.event.client;


import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record Event(
        String id,
        String name,
        LocalDateTime date,
        List<LocalDateTime> rehearsalDates,
        String location,
        List<EventItem> eventItems,
        boolean enabled
) {
}


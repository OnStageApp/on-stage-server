package org.onstage.event.client;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Event(
        String id,
        String title,
        LocalDateTime date,
        LocalDateTime rehearsalDate,
        String location
) {
}


package org.onstage.event.client;


import lombok.Builder;
import org.onstage.event.enums.EventStatus;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Event(
        String id,
        String name,
        LocalDateTime date,
        String location,
        EventStatus eventStatus
) {
}


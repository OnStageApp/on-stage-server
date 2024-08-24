package org.onstage.event.client;


import lombok.Builder;
import org.onstage.enums.EventStatus;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record EventDTO(
        String id,
        String name,
        LocalDateTime date,
        String location,
        EventStatus eventStatus
) {
}


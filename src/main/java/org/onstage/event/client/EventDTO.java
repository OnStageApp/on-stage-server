package org.onstage.event.client;


import lombok.Builder;
import org.onstage.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record EventDTO(
        String id,
        String name,
        LocalDateTime dateTime,
        String location,
        EventStatus eventStatus
) {
}


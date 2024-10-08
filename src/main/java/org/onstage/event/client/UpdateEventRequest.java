package org.onstage.event.client;

import lombok.Builder;
import org.onstage.enums.EventStatus;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record UpdateEventRequest(
        String name,
        LocalDateTime dateTime,
        String location,
        EventStatus eventStatus
) {
}

package org.onstage.event.client;


import lombok.Builder;
import org.onstage.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record Event(
        String id,
        String name,
        LocalDateTime date,
        String location,
        EventStatus eventStatus,
        List<String> userIds
) {
}


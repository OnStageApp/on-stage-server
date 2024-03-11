package org.onstage.event.client;


import lombok.Builder;
import lombok.NonNull;
import org.onstage.event.enums.EventStatus;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record Event(
        String id,
        String name,
        LocalDateTime date,
        List<LocalDateTime> rehearsalDates,
        String location,
        boolean enabled,
        EventStatus eventStatus
) {
}


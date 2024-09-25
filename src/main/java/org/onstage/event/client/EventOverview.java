package org.onstage.event.client;

import lombok.Builder;
import org.onstage.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record EventOverview(
        String id,
        String name,
        EventStatus eventStatus,
        LocalDateTime dateTime,
        String location,
        List<String> userIdsWithPhoto,
        Integer stagerCount
) {
}

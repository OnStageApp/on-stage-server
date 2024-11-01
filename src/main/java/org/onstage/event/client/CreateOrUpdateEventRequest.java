package org.onstage.event.client;

import lombok.Builder;
import org.onstage.enums.EventStatus;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record CreateOrUpdateEventRequest(
        String name,
        LocalDateTime dateTime,
        String location,
        EventStatus eventStatus,
        List<String> teamMemberIds,
        List<CreateRehearsalForEventRequest> rehearsals
) {
}

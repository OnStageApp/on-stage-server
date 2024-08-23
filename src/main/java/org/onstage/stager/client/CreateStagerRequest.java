package org.onstage.stager.client;

import lombok.Builder;
import org.onstage.enums.ParticipationStatus;

import java.util.List;

@Builder(toBuilder = true)
public record CreateStagerRequest(
        String eventId,
        List<String> userIds
) {
}

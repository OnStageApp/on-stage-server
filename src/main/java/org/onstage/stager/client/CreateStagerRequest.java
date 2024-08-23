package org.onstage.stager.client;

import lombok.Builder;
import org.onstage.enums.ParticipationStatus;

@Builder(toBuilder = true)
public record CreateStagerRequest(
        String eventId,
        String userId
) {
}

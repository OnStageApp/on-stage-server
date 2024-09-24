package org.onstage.stager.client;

import lombok.Builder;
import org.onstage.enums.ParticipationStatus;

@Builder(toBuilder = true)
public record StagerDTO(
        String id,
        String eventId,
        String teamMemberId,
        String userId,
        String name,
        ParticipationStatus participationStatus
) {
}

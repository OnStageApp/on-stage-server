package org.onstage.stager.client;

import lombok.Builder;
import org.onstage.enums.ParticipationStatus;

@Builder(toBuilder = true)
public record StagerDTO(
        String id,
        String eventId,
        String userId,
        String name,
        String profilePicture,
        ParticipationStatus participationStatus
) {
}

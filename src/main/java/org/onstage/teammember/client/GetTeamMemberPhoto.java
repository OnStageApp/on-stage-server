package org.onstage.teammember.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetTeamMemberPhoto(
        String id,
        String userId,
        String photoUrl
) {
}

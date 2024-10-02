package org.onstage.team.client;

import lombok.Builder;
import org.onstage.enums.MemberRole;

@Builder(toBuilder = true)
public record GetTeamResponse(
        String id,
        String name,
        Integer membersCount,
        MemberRole role
) {
}

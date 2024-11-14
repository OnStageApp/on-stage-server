package org.onstage.teammember.client;

import lombok.Builder;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.PositionEnum;
import org.onstage.enums.MemberRole;

@Builder(toBuilder = true)
public record GetTeamMembersResponse(
        String id,
        String name,
        String userId,
        String teamId,
        MemberRole role,
        MemberInviteStatus inviteStatus,
        PositionEnum position
) {
}

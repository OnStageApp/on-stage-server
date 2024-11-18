package org.onstage.teammember.client;

import lombok.Builder;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.MemberRole;
import org.onstage.enums.PositionEnum;

@Builder(toBuilder = true)
public record TeamMemberDTO(
        String id,
        String name,
        String userId,
        String teamId,
        MemberRole role,
        MemberInviteStatus inviteStatus,
        PositionEnum position
) {
}

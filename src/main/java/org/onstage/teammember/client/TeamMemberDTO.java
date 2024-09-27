package org.onstage.teammember.client;

import lombok.Builder;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.MemberPosition;
import org.onstage.enums.MemberRole;

@Builder(toBuilder = true)
public record TeamMemberDTO(
        String id,
        String name,
        String userId,
        String teamId,
        MemberRole role,
        MemberInviteStatus inviteStatus,
        MemberPosition position
) {
}

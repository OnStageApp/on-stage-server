package org.onstage.teammember.client;

import lombok.Builder;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.MemberRole;

@Builder(toBuilder = true)
public record TeamMemberIdWithPhoto(
        String id,
        String userId,
        String photoUrl
) {
}

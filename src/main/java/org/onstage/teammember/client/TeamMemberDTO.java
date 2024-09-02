package org.onstage.teammember.client;

import lombok.Builder;
import org.onstage.enums.MemberRight;

@Builder(toBuilder = true)
public record TeamMemberDTO(
        String id,
        String userId,
        String teamId,
        MemberRight memberRight
) {
}

package org.onstage.common.beans;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSecurityContext {
    private String userId;
    private String currentTeamId;
    private String currentTeamMemberId;
}

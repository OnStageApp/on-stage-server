package org.onstage.user.client;

import lombok.Builder;
import org.onstage.enums.PositionEnum;

@Builder(toBuilder = true)
public record UserProfileInfoDTO(
        String name,
        String username,
        String email,
        PositionEnum position,
        String photoUrl
) {
}

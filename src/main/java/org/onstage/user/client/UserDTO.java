package org.onstage.user.client;

import lombok.Builder;
import org.onstage.enums.PositionEnum;
import org.onstage.user.model.UserRole;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record UserDTO(
        String id,
        String name,
        String username,
        String email,
        UserRole role,
        LocalDateTime imageTimestamp,
        String currentTeamId,
        String revenueCatId,
        PositionEnum position
) {
}

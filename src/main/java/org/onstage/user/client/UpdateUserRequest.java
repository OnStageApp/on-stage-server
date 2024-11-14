package org.onstage.user.client;

import lombok.Builder;
import org.onstage.enums.PositionEnum;
import org.onstage.user.model.UserRole;

@Builder(toBuilder = true)
public record UpdateUserRequest(
        String name,
        UserRole role,
        String revenueCatId,
        PositionEnum position
) {
}

package org.onstage.user.client;

import lombok.Builder;
import org.onstage.user.model.UserRole;

@Builder(toBuilder = true)
public record UpdateUserRequest(
        String name,
        UserRole role,
        String revenueCatId
) {
}

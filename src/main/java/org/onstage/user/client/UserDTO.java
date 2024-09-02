package org.onstage.user.client;

import lombok.Builder;
import org.onstage.user.model.UserRole;

@Builder
public record UserDTO(
        String id,
        String name,
        String email,
        UserRole role,
        String profilePicture
) {
}

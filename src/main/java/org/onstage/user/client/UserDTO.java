package org.onstage.user.client;

import lombok.Builder;
import org.onstage.user.model.UserRole;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record UserDTO(
        String id,
        String name,
        String email,
        UserRole role,
        byte[] image,
        LocalDateTime imageTimestamp
) {
}

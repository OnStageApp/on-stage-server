package org.onstage.user.model;

import lombok.Builder;
import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

@With
@Builder
@Document("users")
public record UserEntity(
        String id,
        String name,
        UserRole role
) {
}

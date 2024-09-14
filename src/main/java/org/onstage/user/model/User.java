package org.onstage.user.model;

import lombok.Builder;
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@With
@Builder(toBuilder = true)
@Document("users")
@FieldNameConstants
public record User(
        String id,
        String name,
        String email,
        UserRole role,
        LocalDateTime imageTimestamp,
        String currentTeamId
) {
}

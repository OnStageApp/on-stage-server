package org.onstage.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@With
@Builder(toBuilder = true)
@Document("users")
@FieldNameConstants
@Getter
@Setter
public class User {
    @MongoId
    private String id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime imageTimestamp;
    private String currentTeamId;
    private String stripeCustomerId;
}

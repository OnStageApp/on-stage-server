package org.onstage.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.PositionEnum;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@With
@Builder(toBuilder = true)
@Document("users")
@FieldNameConstants
@Getter
@Setter
public class User extends BaseEntity {
    @MongoId
    private String id;
    private String name;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    private UserRole role;
    private LocalDateTime imageTimestamp;
    private String currentTeamId;
    private PositionEnum position;
}

package org.onstage.team.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("teams")
@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class Team extends BaseEntity {
        @MongoId
        String id;
        String name;
        String leaderId;

}

package org.onstage.stager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.ParticipationStatus;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Getter
@Setter
@Document("stagers")
@FieldNameConstants
@CompoundIndex(name = "eventId_userId_unique", def = "{'eventId': 1, 'userId': 1}", unique = true)
public class Stager extends BaseEntity {
    @MongoId
    String id;
    String eventId;
    String teamMemberId;
    String userId;
    ParticipationStatus participationStatus;
}

package org.onstage.rehearsal.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Document("rehearsals")
@FieldNameConstants
public class Rehearsal extends BaseEntity{
        @MongoId
        String id;
        String name;
        String location;
        LocalDateTime dateTime;
        String eventId;
}

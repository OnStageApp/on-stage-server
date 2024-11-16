package org.onstage.event.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.EventStatus;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Document("events")
@FieldNameConstants
@Getter
@Setter
public class Event extends BaseEntity {
    @MongoId
    String id;
    String name;
    LocalDateTime dateTime;
    String location;
    @Builder.Default
    EventStatus eventStatus = EventStatus.DRAFT;
    String teamId;
    String createdByUser;
}

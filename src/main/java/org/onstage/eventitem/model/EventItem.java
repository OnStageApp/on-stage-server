package org.onstage.eventitem.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.EventItemType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document("eventItems")
@Builder(toBuilder = true)
@Getter
@Setter
@FieldNameConstants
public class EventItem extends BaseEntity {
    @MongoId
    String id;
    String name;
    Integer index;
    EventItemType eventType;
    String songId;
    String eventId;
    List<String> leadVocalIds;
}

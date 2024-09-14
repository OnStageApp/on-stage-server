package org.onstage.event.model;


import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.EventStatus;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Document("events")
@FieldNameConstants
public record Event(
        @MongoId
        String id,
        @NonNull
        String name,
        LocalDateTime dateTime,
        String location,
        EventStatus eventStatus,
        String teamId
) {
    public Event {
        if (eventStatus == null) eventStatus = EventStatus.DRAFT;
    }
}

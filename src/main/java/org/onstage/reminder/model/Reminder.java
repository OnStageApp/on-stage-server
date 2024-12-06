package org.onstage.reminder.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Document("reminders")
@FieldNameConstants
@Data
public class Reminder {
    @MongoId
    String id;
    String eventId;
    String text;
    Integer daysBefore;
    LocalDateTime sendingTime;
    Boolean isSent;
}

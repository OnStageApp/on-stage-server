package org.onstage.reminder.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record ReminderDTO(
        String eventId,
        Integer daysBefore,
        Boolean isSent
) {

}

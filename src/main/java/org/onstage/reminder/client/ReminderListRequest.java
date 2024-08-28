package org.onstage.reminder.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ReminderListRequest(
        List<Integer> daysBefore,
        String eventId
) {
}

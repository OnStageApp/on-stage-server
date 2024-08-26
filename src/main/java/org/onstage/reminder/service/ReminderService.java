package org.onstage.reminder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.model.Event;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.repository.ReminderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {
    private final ReminderRepository reminderRepository;

    private static final String REMINDER_TEXT_TEMPLATE = "%d days left until  %s";

    public List<Reminder> getAll(String eventId) {
        return reminderRepository.getAllByEventId(eventId);
    }

    public Reminder save(Reminder reminder, Event event) {
        LocalDateTime sendingTime = event
                .dateTime()
                .minusDays(reminder.daysBefore())
                .with(LocalTime.of(5, 0));
        reminder = reminder.toBuilder()
                .sendingTime(sendingTime)
                .text(String.format(REMINDER_TEXT_TEMPLATE, reminder.daysBefore(), event.name()))
                .isSent(false)
                .build();

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("Reminder {} has been saved", reminder.id());
        return savedReminder;
    }

    public String delete(String id) {
        return reminderRepository.delete(id);
    }
}

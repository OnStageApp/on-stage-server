package org.onstage.reminder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
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
    private final EventRepository eventRepository;

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

    public List<Reminder> createReminders(List<Integer> daysBefore, String eventId) {
        if (daysBefore.isEmpty()) {
            return List.of();
        }
        var event = eventRepository.findById(eventId).orElseThrow();
        return daysBefore.stream().map(dayBefore ->
                save(Reminder.builder().eventId(eventId).daysBefore(dayBefore).build(), event)
                ).toList();
    }

    public String delete(String id) {
        return reminderRepository.delete(id);
    }
}

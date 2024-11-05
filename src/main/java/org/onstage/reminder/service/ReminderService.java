package org.onstage.reminder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.service.NotificationService;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.repository.ReminderRepository;
import org.onstage.stager.service.StagerService;
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
    private final NotificationService notificationService;
    private final StagerService stagerService;

    private static final String REMINDER_TEXT_TEMPLATE = "%d days left until  %s";

    public List<Reminder> getAllByEventId(String eventId) {
        return reminderRepository.getAllByEventId(eventId);
    }

    public Reminder save(Reminder reminder, Event event) {
        LocalDateTime sendingTime = event
                .getDateTime()
                .minusDays(reminder.daysBefore())
                .with(LocalTime.of(5, 0));
        reminder = reminder.toBuilder()
                .sendingTime(sendingTime)
                .text(String.format(REMINDER_TEXT_TEMPLATE, reminder.daysBefore(), event.getName()))
                .isSent(false)
                .build();

        Reminder savedReminder = reminderRepository.save(reminder);
        log.info("Reminder {} has been saved", savedReminder.id());
        return savedReminder;
    }

    public List<Reminder> createReminders(List<Integer> daysBefore, String eventId) {
        if (daysBefore.isEmpty()) {
            return List.of();
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> BadRequestException.resourceNotFound("event"));

        reminderRepository.deleteAllByEventId(eventId);
        return daysBefore.stream().map(dayBefore ->
                save(Reminder.builder().eventId(eventId).daysBefore(dayBefore).build(), event)
        ).toList();
    }

    public String delete(String id) {
        log.info("Deleting reminder {}", id);
        return reminderRepository.delete(id);
    }

    public void deleteAllByEventId(String eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        log.info("Deleting all reminders for event {}", eventId);
        reminderRepository.deleteAllByEventId(eventId);
    }
}

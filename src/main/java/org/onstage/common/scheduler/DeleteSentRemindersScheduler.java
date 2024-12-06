package org.onstage.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteSentRemindersScheduler {

    private final ReminderRepository reminderRepository;

    @Value(("${cron.enabled}"))
    private Boolean cronEnabled;

    @Scheduled(cron = "${delete.reminders.cron}")
    public void deleteReminders() {
        Thread.currentThread().setName("Cron-Delete-Reminders");
        if (!cronEnabled) {
            log.warn("Delete reminders task didn't run. Cron is disabled!");
        }

        List<Reminder> remindersToDelete = reminderRepository.findRemindersToDelete();

        for (Reminder reminder : remindersToDelete) {
            log.info("Deleting reminder {}", reminder.getId());
            reminderRepository.delete(reminder.getId());
        }

    }
}

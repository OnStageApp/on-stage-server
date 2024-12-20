package org.onstage.common.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.NotificationType;
import org.onstage.enums.PermissionType;
import org.onstage.event.model.Event;
import org.onstage.event.service.EventService;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.plan.service.PlanService;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.repository.ReminderRepository;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendRemindersScheduler {

    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;
    private final StagerService stagerService;
    private final EventService eventService;
    private final PlanService planService;

    @Value(("${cron.enabled}"))
    private Boolean cronEnabled;

    @Scheduled(cron = "${send.reminders.cron}")
    public void sendReminders() {
        Thread.currentThread().setName("Cron-Send-Reminders");
        if (!cronEnabled) {
            log.warn("Send reminders task didn't run. Cron is disabled!");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Reminder> remindersToSend = reminderRepository.findRemindersToSend(now);

        for (Reminder reminder : remindersToSend) {
            List<Stager> stagers = stagerService.getAllByEventId(reminder.getEventId());
            Event event = eventService.getById(reminder.getEventId());
            planService.checkPermission(PermissionType.REMINDERS, event.getTeamId());

            String description = String.format("%d days left until %s", reminder.getDaysBefore(), event.getName());
            String title = "Reminder";

            log.info("Sending reminder {}", reminder.getId());
            stagers.forEach(stager -> notificationService.sendNotificationToUser(NotificationType.REMINDER, stager.getUserId(), description, title, event.getTeamId(), NotificationParams.builder().teamId(event.getTeamId()).eventId(event.getId()).build()));
            reminder.setIsSent(true);
            reminderRepository.save(reminder);
        }

    }
}

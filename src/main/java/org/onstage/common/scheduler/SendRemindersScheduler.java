package org.onstage.common.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.notification.service.PushNotificationService;
import org.onstage.reminder.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendRemindersScheduler {

    private final ReminderRepository reminderRepository;
    private final PushNotificationService pushNotificationService;

    @Value(("${cron.enabled}"))
    private Boolean cronEnabled;

    @Scheduled(cron = "${send.reminders.cron}")
    public void sendReminders() {
        Thread.currentThread().setName("Cron-Send-Reminders");
        if (!cronEnabled) {
            log.warn("Send reminders task didn't run. Cron is disabled!");
        }

        //TODO fix this
//        LocalDateTime now = LocalDateTime.now();
//        List<Reminder> remindersToSend = reminderRepository.findRemindersToSend(now);
//
//        for (Reminder reminder : remindersToSend) {
//            log.info("Sending reminder {}", reminder.id());
//            //TODO getDeviceToken from user
//            pushNotificationService.sendPush(reminder, "eLTv45H38UMdnE-9UzNnqm:APA91bFPHzaNmrdvYozG1eTUxjvtneFlIzLAxcJpEy7ZRQI1X4k_3iegpSXUVzhw_jFRGwUZ30hZbBqmf_v1j8nMVBbFxOLAb_RspYVojwG0Pyi6IKhe1ZChma3sp0XTRVAkWlE2KG4S");
//            reminder.toBuilder().isSent(true);
//            reminderRepository.save(reminder);
//        }

    }
}

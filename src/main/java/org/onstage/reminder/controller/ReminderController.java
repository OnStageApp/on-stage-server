package org.onstage.reminder.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.service.FirebaseService;
import org.onstage.event.service.EventService;
import org.onstage.reminder.client.ReminderDTO;
import org.onstage.reminder.client.ReminderListRequest;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.model.mapper.ReminderMapper;
import org.onstage.reminder.service.ReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reminders")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;
    private final FirebaseService firebaseService;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<ReminderDTO>> getAll(@RequestParam(name = "eventId") String eventId) {
        return ResponseEntity.ok(reminderMapper.toDtoList(reminderService.getAllByEventId(eventId)));
    }

    @PostMapping
    public ResponseEntity<List<Reminder>> create(@RequestBody final ReminderListRequest request) {
        return ResponseEntity.ok((reminderService.createReminders(request.daysBefore(), request.eventId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(reminderService.delete(id));
    }

    @PostMapping("/test-notification")
    public ResponseEntity<Void> testNotification() {
        firebaseService.sendNotification(Reminder.builder().text("You were invited to join El Shaddai Organisation").build(), "eLTv45H38UMdnE-9UzNnqm:APA91bFPHzaNmrdvYozG1eTUxjvtneFlIzLAxcJpEy7ZRQI1X4k_3iegpSXUVzhw_jFRGwUZ30hZbBqmf_v1j8nMVBbFxOLAb_RspYVojwG0Pyi6IKhe1ZChma3sp0XTRVAkWlE2KG4S");
        return ResponseEntity.ok().build();
    }
}


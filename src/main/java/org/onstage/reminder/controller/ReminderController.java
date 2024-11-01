package org.onstage.reminder.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.event.service.EventService;
import org.onstage.notification.service.PushNotificationService;
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

}


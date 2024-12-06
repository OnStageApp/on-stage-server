package org.onstage.reminder.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.enums.PermissionType;
import org.onstage.plan.service.PlanService;
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
    private final UserSecurityContext userSecurityContext;
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<ReminderDTO>> getAll(@RequestParam(name = "eventId") String eventId) {
        return ResponseEntity.ok(reminderMapper.toDtoList(reminderService.getAllByEventId(eventId)));
    }

    @PostMapping
    public ResponseEntity<List<Reminder>> create(@RequestBody final ReminderListRequest request) {
//        planService.checkPermission(PermissionType.REMINDERS, userSecurityContext.getCurrentTeamId());
        return ResponseEntity.ok((reminderService.createReminders(request.daysBefore(), request.eventId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(reminderService.delete(id));
    }

}


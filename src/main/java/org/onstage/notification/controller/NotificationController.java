package org.onstage.notification.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.notification.client.NotificationDTO;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.mapper.NotificationMapper;
import org.onstage.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper mapper;
    private final UserSecurityContext userSecurityContext;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@RequestBody NotificationFilter filter) {
        String userId = userSecurityContext.getUserId();
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId, filter).stream()
                .map(mapper::toDTO)
                .toList());
    }

    @PutMapping("/viewed")
    public ResponseEntity<Void> markNotificationsAsViewed() {
        final String userId = userSecurityContext.getUserId();
        notificationService.markAllNotificationsAsViewed(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotification(@PathVariable String id, @RequestBody NotificationDTO request) {
        notificationService.updateNotification(id, mapper.toEntity(request));
        return ResponseEntity.ok().build();
    }
}

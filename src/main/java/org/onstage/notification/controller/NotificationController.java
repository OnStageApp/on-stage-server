package org.onstage.notification.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.notification.client.NotificationDTO;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.Notification;
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

    @GetMapping
    public List<NotificationDTO> getAllNotifications(NotificationFilter filter) {
        return notificationService.getAllNotifications(filter).stream()
                .map(mapper::toApi)
                .toList();
    }

    @PostMapping
    public List<NotificationDTO> getAllNotificationsPost(@RequestBody NotificationFilter filter) {
        return getAllNotifications(filter);
    }

    @PutMapping("/markAsViewed")
    public void markNotificationsAsViewed() {
        notificationService.markAllNotificationsAsViewed();
    }
    @PutMapping("/{id}")
    public void updateNotification(@PathVariable String id, @RequestBody NotificationDTO request) {
        final Notification existingNotification = notificationService.getById(id);
        notificationService.updateNotification(existingNotification, request);
    }
}

package org.onstage.notification.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.notification.client.GetNotificationsResponse;
import org.onstage.notification.client.NotificationDTO;
import org.onstage.notification.model.PaginatedNotifications;
import org.onstage.notification.model.mapper.NotificationMapper;
import org.onstage.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper mapper;
    private final UserSecurityContext userSecurityContext;
    private final NotificationMapper notificationMapper;

    @GetMapping
    public ResponseEntity<GetNotificationsResponse> getAllNotifications(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        String userId = userSecurityContext.getUserId();
        String currentTeamId = userSecurityContext.getCurrentTeamId();

        PaginatedNotifications paginatedResponse = notificationService.getNotificationsForUser(userId, currentTeamId, offset, limit);

        return ResponseEntity.ok(notificationMapper.toGetAllNotificationsResponse(paginatedResponse));
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

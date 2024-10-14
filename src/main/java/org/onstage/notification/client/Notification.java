package org.onstage.notification.client;

import lombok.Builder;

@Builder
public record Notification(
        String notificationId,
        String description,
        NotificationType type,
        NotificationStatus status,
        String userId
) {
}

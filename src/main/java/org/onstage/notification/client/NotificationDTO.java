package org.onstage.notification.client;

import lombok.Builder;

@Builder
public record NotificationDTO(
        String notificationId,
        String description,
        NotificationType type,
        NotificationStatus status,
        NotificationActionStatus actionStatus,
        String eventId,
        String userId,
        String stagerId
) {
}

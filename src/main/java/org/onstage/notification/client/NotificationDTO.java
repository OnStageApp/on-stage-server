package org.onstage.notification.client;

import lombok.Builder;
import org.onstage.notification.model.NotificationParams;

@Builder
public record NotificationDTO(
        String notificationId,
        String title,
        String description,
        NotificationActionStatus actionStatus,
        NotificationParams params,
        NotificationType type,
        NotificationStatus status,
        String userToNotify
) {
}

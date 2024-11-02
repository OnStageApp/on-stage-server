package org.onstage.notification.client;

import java.time.LocalDateTime;

public record NotificationFilter(
        NotificationStatus status,
        NotificationType type,
        String userId,
        LocalDateTime since
) {
}

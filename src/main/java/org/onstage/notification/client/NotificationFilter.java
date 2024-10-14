package org.onstage.notification.client;

public record NotificationFilter(
        NotificationStatus status,
        NotificationType type,
        String userId
) {
}

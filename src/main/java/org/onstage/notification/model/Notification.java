package org.onstage.notification.model;

import lombok.Builder;
import lombok.With;
import org.onstage.notification.client.NotificationStatus;
import org.onstage.notification.client.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@With
@Builder
@Document("notifications")
public record Notification(
        @Id
        String notificationId,
        String description,
        String userId,
        NotificationType type,
        NotificationStatus status

) {
}

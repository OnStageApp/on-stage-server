package org.onstage.notification.client;

import lombok.Builder;
import org.onstage.enums.NotificationActionStatus;
import org.onstage.notification.model.NotificationParams;
import org.onstage.enums.NotificationStatus;
import org.onstage.enums.NotificationType;

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

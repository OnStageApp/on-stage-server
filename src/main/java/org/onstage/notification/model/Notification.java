package org.onstage.notification.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.notification.client.NotificationActionStatus;
import org.onstage.notification.client.NotificationStatus;
import org.onstage.notification.client.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@FieldNameConstants
@Getter
@Setter
@Document("notifications")
public class Notification extends BaseEntity {
    @Id
    private String notificationId;
    private String title;
    private String description;
    private NotificationActionStatus actionStatus;
    private NotificationParams params;
    private NotificationType type;
    private NotificationStatus status;
    private String userToNotify;
}

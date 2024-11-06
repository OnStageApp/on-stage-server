package org.onstage.notification.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.NotificationActionStatus;
import org.onstage.enums.NotificationStatus;
import org.onstage.enums.NotificationType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@FieldNameConstants
@Getter
@Setter
@Document("notifications")
public class Notification extends BaseEntity {
    @MongoId
    private String notificationId;
    private String title;
    private String description;
    private NotificationActionStatus actionStatus;
    private NotificationParams params;
    private NotificationType type;
    private NotificationStatus status;
    private String userToNotify;
}

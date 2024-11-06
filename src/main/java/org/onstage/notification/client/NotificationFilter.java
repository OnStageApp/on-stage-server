package org.onstage.notification.client;

import lombok.*;
import org.onstage.enums.NotificationStatus;

@Getter
@Setter
@RequiredArgsConstructor
public class NotificationFilter {
    private NotificationStatus status;
}

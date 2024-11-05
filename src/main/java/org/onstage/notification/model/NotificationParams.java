package org.onstage.notification.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class NotificationParams {
    private String eventId;
    private String userId;
    private String teamId;
    private String stagerId;
    private String teamMemberId;
    private String eventItemId;
    private LocalDateTime date;
    private List<String> usersWithPhoto;
}

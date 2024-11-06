package org.onstage.notification.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GetNotificationsResponse(List<NotificationDTO> notifications, boolean hasMore) {
}

package org.onstage.notification.model;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record PaginatedNotifications(List<Notification> notifications, boolean hasMore) {
}

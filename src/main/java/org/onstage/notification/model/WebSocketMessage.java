package org.onstage.notification.model;

import lombok.Builder;

@Builder
public record WebSocketMessage(
        String content
) {
}

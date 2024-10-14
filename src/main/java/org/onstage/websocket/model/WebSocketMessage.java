package org.onstage.websocket.model;

import lombok.Builder;

@Builder
public record WebSocketMessage(
        String content
) {
}

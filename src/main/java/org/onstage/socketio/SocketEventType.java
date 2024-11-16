package org.onstage.socketio;

import lombok.Getter;

@Getter
public enum SocketEventType {
    NOTIFICATION,
    SUBSCRIPTION,
    TEAM_CHANGED
}

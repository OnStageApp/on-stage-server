package org.onstage.user.client;

import lombok.Builder;

@Builder
public record LoginRequest(
        String firebaseToken
) {
}

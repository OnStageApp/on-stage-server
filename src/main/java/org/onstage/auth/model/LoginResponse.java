package org.onstage.auth.model;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}

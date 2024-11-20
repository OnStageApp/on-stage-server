package org.onstage.auth.model;

import lombok.Builder;

@Builder
public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {}

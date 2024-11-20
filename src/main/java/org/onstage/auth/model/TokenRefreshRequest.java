package org.onstage.auth.model;

import lombok.Builder;

@Builder
public record TokenRefreshRequest(
        String refreshToken
) {}

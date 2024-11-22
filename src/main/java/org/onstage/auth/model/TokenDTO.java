package org.onstage.auth.model;

import lombok.Builder;

@Builder
public record TokenDTO(
        String accessToken,
        String refreshToken
) {
}

package org.onstage.team.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record TeamDTO(
        String id,
        String name
) {
}

package org.onstage.artist.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record Artist(
        String id,
        String name
) {
}

package org.onstage.artist.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record ArtistDTO(
        String id,
        String name,
        String imageUrl
) {
}

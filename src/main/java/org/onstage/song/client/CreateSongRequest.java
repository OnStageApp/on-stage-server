package org.onstage.song.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record CreateSongRequest(
        String title,
        String lyrics,
        Integer tempo,
        String key,
        String artistId
) {
}

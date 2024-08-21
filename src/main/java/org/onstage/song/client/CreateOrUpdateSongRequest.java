package org.onstage.song.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record CreateOrUpdateSongRequest(
        String title,
        String lyrics,
        Integer tempo,
        String key,
        String artistId
) {
}

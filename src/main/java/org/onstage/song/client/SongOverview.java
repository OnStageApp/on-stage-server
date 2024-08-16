package org.onstage.song.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record SongOverview(
        String id,
        String title,
        String artistId,
        String key,
        Integer tempo
) {
}

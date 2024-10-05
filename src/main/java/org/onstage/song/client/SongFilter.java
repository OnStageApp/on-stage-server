package org.onstage.song.client;

import lombok.Builder;
import org.onstage.song.model.SongKey;

@Builder
public record SongFilter(
        String search,
        String artistId,
        String genres
) {
}

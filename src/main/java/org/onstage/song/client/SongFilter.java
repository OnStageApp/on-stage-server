package org.onstage.song.client;

import lombok.Builder;
import org.onstage.enums.KeysEnum;

@Builder
public record SongFilter(
        String search,
        String artistId,
        KeysEnum key,
        String genres
) {
}

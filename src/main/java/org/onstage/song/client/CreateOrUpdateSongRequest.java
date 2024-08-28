package org.onstage.song.client;

import lombok.Builder;
import org.onstage.enums.KeysEnum;

@Builder(toBuilder = true)
public record CreateOrUpdateSongRequest(
        String title,
        String lyrics,
        Integer tempo,
        KeysEnum key,
        String artistId
) {
}

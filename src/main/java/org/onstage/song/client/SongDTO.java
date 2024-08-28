package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.enums.KeysEnum;

@Builder(toBuilder = true)
public record SongDTO(
        String id,
        String title,
        String lyrics,
        Integer tempo,
        KeysEnum key,
        ArtistDTO artist
) {
}

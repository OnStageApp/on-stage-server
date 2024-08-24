package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;

@Builder(toBuilder = true)
public record SongDTO(
        String id,
        String title,
        String lyrics,
        Integer tempo,
        String key,
        ArtistDTO artist
) {
}

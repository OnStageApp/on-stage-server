package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;

@Builder(toBuilder = true)
public record SongOverview(
        String id,
        String title,
        ArtistDTO artist,
        String key,
        Integer tempo
) {
}

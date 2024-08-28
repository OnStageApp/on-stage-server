package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.enums.KeysEnum;

@Builder(toBuilder = true)
public record SongOverview(
        String id,
        String title,
        ArtistDTO artist,
        KeysEnum key,
        Integer tempo
) {
}

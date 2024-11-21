package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.enums.GenreEnum;
import org.onstage.enums.ThemeEnum;
import org.onstage.song.model.SongKey;

@Builder(toBuilder = true)
public record SongOverview(
        String id,
        String title,
        ArtistDTO artist,
        SongKey key,
        Integer tempo,
        String teamId,
        ThemeEnum theme,
        GenreEnum genre
) {
}

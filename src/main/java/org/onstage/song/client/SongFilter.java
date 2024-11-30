package org.onstage.song.client;

import lombok.Builder;
import org.onstage.enums.GenreEnum;
import org.onstage.enums.ThemeEnum;

@Builder
public record SongFilter(
        String search,
        String artistId,
        Boolean includeOnlyTeamSongs,
        TempoRange tempoRange,
        GenreEnum genre,
        ThemeEnum theme
) {
}

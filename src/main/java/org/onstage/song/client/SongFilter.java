package org.onstage.song.client;

import lombok.Builder;

@Builder
public record SongFilter(
        String search,
        String artistId,
        String genres,
        Boolean includeOnlyTeamSongs,
        TempoRange tempoRange
) {
}

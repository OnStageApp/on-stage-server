package org.onstage.song.client;

import lombok.Builder;
import org.onstage.enums.GenreEnum;
import org.onstage.enums.StructureItemEnum;
import org.onstage.enums.ThemeEnum;
import org.onstage.song.model.RawSongSection;
import org.onstage.song.model.SongKey;

import java.util.List;

@Builder(toBuilder = true)
public record CreateOrUpdateSongRequest(
        String title,
        List<StructureItemEnum> structure,
        List<RawSongSection> rawSections,
        SongKey originalKey,
        String artistId,
        ThemeEnum theme,
        GenreEnum genre,
        TempoRange tempo
) {
}

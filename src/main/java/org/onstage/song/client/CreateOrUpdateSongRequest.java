package org.onstage.song.client;

import lombok.Builder;
import org.onstage.enums.StructureItemEnum;
import org.onstage.song.model.RawSongSection;
import org.onstage.song.model.SongKey;

import java.util.List;

@Builder(toBuilder = true)
public record CreateOrUpdateSongRequest(
        String title,
        List<StructureItemEnum> structure,
        List<RawSongSection> rawSections,
        Integer tempo,
        SongKey originalKey,
        String artistId
) {
}

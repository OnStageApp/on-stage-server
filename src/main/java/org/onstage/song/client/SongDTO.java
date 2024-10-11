package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.enums.StructureItemEnum;
import org.onstage.song.model.RawSongSection;
import org.onstage.song.model.SongKey;

import java.util.List;

@Builder(toBuilder = true)
public record SongDTO(
        String id,
        String title,
        List<StructureItemEnum> structure,
        List<RawSongSection> rawSections,
        Integer tempo,
        SongKey key,
        SongKey originalKey,
        ArtistDTO artist,
        String teamId
) {
}

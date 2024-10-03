package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.enums.KeysEnum;
import org.onstage.enums.StructureItemEnum;
import org.onstage.song.model.RawSongSection;

import java.util.List;

@Builder(toBuilder = true)
public record SongDTO(
        String id,
        String title,
        List<StructureItemEnum> structure,
        List<RawSongSection> rawSections,
        Integer tempo,
        KeysEnum key,
        ArtistDTO artist
) {
}

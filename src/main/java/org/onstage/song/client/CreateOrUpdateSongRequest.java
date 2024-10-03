package org.onstage.song.client;

import lombok.Builder;
import org.onstage.enums.KeysEnum;
import org.onstage.enums.StructureItemEnum;

import java.util.List;

@Builder(toBuilder = true)
public record CreateOrUpdateSongRequest(
        String title,
        List<StructureItemEnum> structure,
        String rawSections,
        Integer tempo,
        KeysEnum key,
        String artistId
) {
}

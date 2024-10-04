package org.onstage.songconfig.client;

import lombok.Builder;
import org.onstage.enums.StructureItemEnum;
import org.onstage.song.model.SongKey;

import java.util.List;

@Builder(toBuilder = true)
public record SongConfigDTO(
        String songId,
        String teamId,
        SongKey key,
        List<StructureItemEnum> structure,
        Boolean isCustom
) {
}

package org.onstage.songconfig.client;

import lombok.Builder;
import org.onstage.enums.KeysEnum;
import org.onstage.enums.StructureItemEnum;

import java.util.List;

@Builder(toBuilder = true)
public record SongConfigDTO(
        String songId,
        String teamId,
        KeysEnum key,
        List<StructureItemEnum> structure,
        Boolean isCustom
) {
}

package org.onstage.songversion.client;

import lombok.Builder;
import org.onstage.enums.KeysEnum;

@Builder(toBuilder = true)
public record SongConfigDTO(
        String songId,
        String teamId,
        KeysEnum key,
        String lyrics,
        Boolean isCustom
) {
}

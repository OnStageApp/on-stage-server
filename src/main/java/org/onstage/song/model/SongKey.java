package org.onstage.song.model;

import lombok.Builder;
import org.onstage.enums.ChordEnum;

@Builder(toBuilder = true)
public record SongKey(
        ChordEnum chord,
        Boolean isMajor,
        Boolean isSharp
) {
}

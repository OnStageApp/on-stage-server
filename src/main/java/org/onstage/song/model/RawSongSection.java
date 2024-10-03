package org.onstage.song.model;

import lombok.Builder;
import org.onstage.enums.StructureItemEnum;

@Builder
public record RawSongSection(
        StructureItemEnum structureItem,
        String content
) {
}

package org.onstage.songconfig.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.KeysEnum;
import org.onstage.enums.StructureItemEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "songConfig")
@CompoundIndex(name = "songId_teamId_unique", def = "{'songId': 1, 'teamId': 1}", unique = true)
@Builder(toBuilder = true)
@FieldNameConstants
public record SongConfig(
        @MongoId
        String id,
        String songId,
        String teamId,
        KeysEnum key,
        List<StructureItemEnum> structure,
        Boolean isCustom
) {
}

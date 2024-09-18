package org.onstage.songconfig.model;

import lombok.Builder;
import org.onstage.enums.KeysEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "songConfig")
@CompoundIndex(name = "songId_teamId_unique", def = "{'songId': 1, 'teamId': 1}", unique = true)
@Builder(toBuilder = true)
public record SongConfig(
        @MongoId
        String id,
        String songId,
        String teamId,
        KeysEnum key,
        String lyrics,
        Boolean isCustom
) {
}

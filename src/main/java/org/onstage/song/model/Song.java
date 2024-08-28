package org.onstage.song.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.KeysEnum;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Builder
@Document("songs")
@FieldNameConstants
public record Song(
        @MongoId
        String id,
        String title,
        String lyrics,
        Integer tempo,
        KeysEnum key,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String artistId

) {

}

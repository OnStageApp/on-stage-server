package org.onstage.song.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@Document("songs")
@FieldNameConstants
public record SongEntity(
        @MongoId
        String id,
        String title,
        String lyrics,
        String tab,
        String key,
        String createdAt,
        String updatedAt,
        String artist

) {

}

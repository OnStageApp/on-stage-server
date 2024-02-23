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
        String name
) {

}

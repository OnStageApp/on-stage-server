package org.onstage.song.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.KeysEnum;
import org.onstage.enums.StructureItemEnum;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Document("songs")
@FieldNameConstants
public record Song(
        @MongoId
        String id,
        String title,
        List<StructureItemEnum> structure,
        String rawSections,
        Integer tempo,
        KeysEnum key,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String artistId

) {

}

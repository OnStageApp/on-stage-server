package org.onstage.artist.model;


import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@Document("artists")
@FieldNameConstants
public record Artist(
        @MongoId
        String id,
        String name,
        String imageUrl
) {

}

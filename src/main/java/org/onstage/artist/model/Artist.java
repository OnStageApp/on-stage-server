package org.onstage.artist.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@Getter
@Setter
@Document("artists")
@FieldNameConstants
public class Artist {
    @MongoId
    String id;
    String name;
    String imageUrl;
}

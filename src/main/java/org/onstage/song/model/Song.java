package org.onstage.song.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.GenreEnum;
import org.onstage.enums.StructureItemEnum;
import org.onstage.enums.ThemeEnum;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document("songs")
@FieldNameConstants
@Getter
@Setter
@Builder(toBuilder = true)
public class Song extends BaseEntity {
    @MongoId
    String id;
    String title;
    List<StructureItemEnum> structure;
    List<RawSongSection> rawSections;
    SongKey originalKey;
    String artistId;
    String teamId;
    ThemeEnum theme;
    @Deprecated
    GenreEnum genre;
    Integer tempo;
}

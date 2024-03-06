package org.onstage.artist.model.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.onstage.event.model.EventItemEntity;
import org.onstage.artist.client.Artist;
import org.onstage.artist.model.ArtistEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    Artist toApi(ArtistEntity entity);

    @Mapping(target = "id", ignore = true)
    ArtistEntity toDb(Artist request);

    List<Artist> toApiList(List<ArtistEntity> entities);

    List<ArtistEntity> toDbList(List<Artist> requests);

    ArtistEntity toEntity(String id);
}

package org.onstage.artist.model.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.onstage.artist.client.Artist;
import org.onstage.artist.model.ArtistEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    Artist toDto(ArtistEntity entity);

    @Mapping(target = "id", ignore = true)
    ArtistEntity toEntity(Artist request);

    List<Artist> toDtoList(List<ArtistEntity> entities);
}

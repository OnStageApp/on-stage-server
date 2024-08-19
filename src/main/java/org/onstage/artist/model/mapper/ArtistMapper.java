package org.onstage.artist.model.mapper;

import org.onstage.artist.client.Artist;
import org.onstage.artist.model.ArtistEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtistMapper {
    public Artist toDto(ArtistEntity entity) {
        return Artist.builder()
                .id(entity.id())
                .name(entity.name())
                .build();
    }

    public ArtistEntity toEntity(Artist request) {
        return ArtistEntity.builder()
                .name(request.name())
                .build();
    }

    public List<Artist> toDtoList(List<ArtistEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
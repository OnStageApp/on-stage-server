package org.onstage.artist.model.mapper;

import org.onstage.artist.client.ArtistDTO;
import org.onstage.artist.model.Artist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtistMapper {
    public ArtistDTO toDto(Artist entity) {
        return ArtistDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .imageUrl(null)
                .build();
    }

    public Artist toEntity(ArtistDTO request) {
        return Artist.builder()
                .name(request.name())
                .imageUrl(null)
                .build();
    }

    public List<ArtistDTO> toDtoList(List<Artist> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
package org.onstage.artist.model.mapper;

import org.onstage.artist.client.ArtistDTO;
import org.onstage.artist.client.GetAllArtistsResponse;
import org.onstage.artist.client.PaginatedArtistResponse;
import org.onstage.artist.model.Artist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistMapper {
    public ArtistDTO toDto(Artist entity) {
        return ArtistDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public Artist toEntity(ArtistDTO request) {
        return Artist.builder()
                .name(request.name())
                .build();
    }

    public List<ArtistDTO> toDtoList(List<Artist> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public GetAllArtistsResponse toGetAllArtists(PaginatedArtistResponse paginatedArtistResponse) {
        List<ArtistDTO> artistDTOS = paginatedArtistResponse.artists().parallelStream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return GetAllArtistsResponse.builder()
                .artists(artistDTOS)
                .hasMore(paginatedArtistResponse.hasMore())
                .build();
    }
}
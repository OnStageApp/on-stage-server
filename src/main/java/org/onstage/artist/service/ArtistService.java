package org.onstage.artist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.artist.client.Artist;
import org.onstage.artist.model.ArtistEntity;
import org.onstage.artist.repository.ArtistRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistEntity getById(String id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist with id:%s was not found".formatted(id)));
    }

    public List<ArtistEntity> getAll() {
        return artistRepository.getAll();
    }

    public ArtistEntity save(ArtistEntity artist) {
        ArtistEntity savedArtist = artistRepository.save(artist);
        log.info("Artist {} has been saved", savedArtist.id());
        return savedArtist;
    }

    public ArtistEntity update(String id, Artist request) {
        ArtistEntity existingArtist = getById(id);
        ArtistEntity updatedArtist = updateArtistFromDTO(existingArtist, request);
        return save(updatedArtist);
    }

    private ArtistEntity updateArtistFromDTO(ArtistEntity existingArtist, Artist request) {
        return ArtistEntity.builder()
                .id(existingArtist.id())
                .name(request.name())
                .build();
    }
}

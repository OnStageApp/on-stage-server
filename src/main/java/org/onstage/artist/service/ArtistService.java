package org.onstage.artist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.artist.model.Artist;
import org.onstage.artist.repository.ArtistRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final ArtistRepository artistRepository;

    public Artist getById(String id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist with id:%s was not found".formatted(id)));
    }

    public List<Artist> getAll() {
        return artistRepository.getAll();
    }

    public Artist save(Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        log.info("Artist {} has been saved", savedArtist.id());
        return savedArtist;
    }

    public Artist update(String id, ArtistDTO request) {
        Artist existingArtist = getById(id);
        Artist updatedArtist = updateArtistFromDTO(existingArtist, request);
        return save(updatedArtist);
    }

    private Artist updateArtistFromDTO(Artist existingArtist, ArtistDTO request) {
        return Artist.builder()
                .id(existingArtist.id())
                .name(request.name())
                .build();
    }
}

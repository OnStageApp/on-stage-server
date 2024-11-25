package org.onstage.artist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.artist.model.Artist;
import org.onstage.artist.repository.ArtistRepository;
import org.onstage.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final ArtistRepository artistRepository;

    public Artist getById(String id) {
        return artistRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("artist"));
    }

    public List<Artist> getAll() {
        return artistRepository.getAll();
    }

    public Artist save(Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        log.info("Artist {} has been saved", savedArtist.getId());
        return savedArtist;
    }

    public Artist update(Artist existingArtist, Artist request) {
        log.info("Updating artist {} with request {}", existingArtist.getId(), request);
        existingArtist.setName(request.getName() == null ? existingArtist.getName() : request.getName());
        existingArtist.setImageUrl(request.getImageUrl() == null ? existingArtist.getImageUrl() : request.getImageUrl());
        return save(existingArtist);
    }
}

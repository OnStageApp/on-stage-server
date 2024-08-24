package org.onstage.artist.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.model.Artist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArtistRepository {
    private final ArtistRepo artistRepo;

    public Optional<Artist> findById(String id) {
        return artistRepo.findById(id);
    }

    public List<Artist> getAll() {
        return artistRepo.findAll();
    }

    public Artist save(Artist artist) {
        return artistRepo.save(artist);
    }
}

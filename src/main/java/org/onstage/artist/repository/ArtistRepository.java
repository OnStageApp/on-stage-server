package org.onstage.artist.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.model.ArtistEntity;
import org.onstage.song.model.SongEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArtistRepository {
    private final ArtistRepo artistRepo;

    public Optional<ArtistEntity> findById(String id) {
        return artistRepo.findById(id);
    }

    public List<ArtistEntity> getAll() {
        return artistRepo.findAll();
    }

    public ArtistEntity create(ArtistEntity artist) {
        return artistRepo.save(artist);
    }

    public ArtistEntity save(ArtistEntity artist) {
        return artistRepo.save(artist);
    }
}

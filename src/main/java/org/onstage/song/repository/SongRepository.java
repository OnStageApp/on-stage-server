package org.onstage.song.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.Song;
import org.onstage.song.model.SongEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SongRepository {
    private final SongRepo songRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<SongEntity> findById(String id) {
        return songRepo.findById(id);
    }

    public List<SongEntity> getAll() {
        return songRepo.findAll();
    }

    public SongEntity create(SongEntity song) {
        return songRepo.save(song);
    }

    public SongEntity save(SongEntity event) {
        return songRepo.save(event);
    }
}

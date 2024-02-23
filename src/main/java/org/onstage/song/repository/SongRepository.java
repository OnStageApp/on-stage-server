package org.onstage.song.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.song.model.SongEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SongRepository {
    private final SongRepo songRepo;
    private final MongoTemplate mongoTemplate;

    public List<SongEntity> getAll() {
        return songRepo.findAll();
    }

    public void create(SongEntity song) {
        songRepo.save(song);
    }
}

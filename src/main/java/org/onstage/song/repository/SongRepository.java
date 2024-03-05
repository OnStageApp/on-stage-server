package org.onstage.song.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.song.model.SongEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.onstage.song.model.SongEntity.Fields.lyrics;
import static org.onstage.song.model.SongEntity.Fields.title;

@Component
@RequiredArgsConstructor
public class SongRepository {
    private final SongRepo songRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<SongEntity> findById(String id) {
        return songRepo.findById(id);
    }

    public List<SongEntity> getAll(String search) {
        if (search != null && !search.isEmpty()) {
            Criteria criteria = new Criteria().orOperator(
                    Criteria.where(title).regex(search, "i"),
                    Criteria.where(lyrics).regex(search, "i")
            );
            Query query = new Query(criteria);
            query.with(Sort.by(Sort.Order.asc("title"), Sort.Order.asc("lyrics")));
            return mongoTemplate.find(query, SongEntity.class);
        }
        return songRepo.findAll();
    }

    public SongEntity create(SongEntity song) {
        return songRepo.save(song);
    }

    public SongEntity save(SongEntity event) {
        return songRepo.save(event);
    }
}

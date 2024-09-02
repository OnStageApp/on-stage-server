package org.onstage.artist.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.model.Artist;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@RequiredArgsConstructor
public class ArtistRepository {
    private final ArtistRepo artistRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Artist> findById(String id) {
        return artistRepo.findById(id);
    }

    public List<Artist> getAll() {
        return artistRepo.findAll();
    }

    public Artist save(Artist artist) {
        return artistRepo.save(artist);
    }

    public Artist getById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        return mongoTemplate.findOne(query(criteria), Artist.class);
    }
}

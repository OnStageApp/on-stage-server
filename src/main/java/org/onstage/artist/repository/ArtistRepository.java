package org.onstage.artist.repository;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.onstage.artist.client.GetArtistFilter;
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

    public List<Artist> getAll(GetArtistFilter filter) {
        Criteria criteria = new Criteria();
        if (filter != null && Strings.isNotEmpty(filter.search())) {
            criteria.and("name").regex(filter.search(), "i");
        }
        return mongoTemplate.find(query(criteria), Artist.class);
    }

    public Artist save(Artist artist) {
        return artistRepo.save(artist);
    }

    public Artist getById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        return mongoTemplate.findOne(query(criteria), Artist.class);
    }

    public Artist findByName(String artistId) {
        Criteria criteria = Criteria.where(Artist.Fields.name).is(artistId);
        return mongoTemplate.findOne(query(criteria), Artist.class);
    }
}

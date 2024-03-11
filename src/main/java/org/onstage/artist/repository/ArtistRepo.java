package org.onstage.artist.repository;

import org.onstage.artist.model.ArtistEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepo extends MongoRepository<ArtistEntity, String> {
}

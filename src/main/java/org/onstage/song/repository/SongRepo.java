package org.onstage.song.repository;

import org.onstage.song.model.SongEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepo extends MongoRepository<SongEntity,String> {
}

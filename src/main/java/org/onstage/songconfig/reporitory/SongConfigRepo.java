package org.onstage.songconfig.reporitory;

import org.onstage.songconfig.model.SongConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongConfigRepo extends MongoRepository<SongConfig, String> {
}

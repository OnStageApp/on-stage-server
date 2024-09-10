package org.onstage.songversion.reporitory;

import org.onstage.songversion.model.SongConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongConfigRepo extends MongoRepository<SongConfig, String> {
}

package org.onstage.songconfig.reporitory;

import lombok.RequiredArgsConstructor;
import org.onstage.songconfig.model.SongConfig;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SongConfigRepository {
    private final MongoTemplate mongoTemplate;
    private final SongConfigRepo songConfigRepo;

    public SongConfig getBySongAndTeam(String songId, String teamId) {
        Criteria criteria = Criteria.where(SongConfig.Fields.songId).is(songId).and(SongConfig.Fields.teamId).is(teamId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, SongConfig.class);
    }

    public SongConfig save(SongConfig songConfig) {
        return songConfigRepo.save(songConfig);
    }

    public void delete(SongConfig existingConfig) {
        songConfigRepo.delete(existingConfig);
    }
}

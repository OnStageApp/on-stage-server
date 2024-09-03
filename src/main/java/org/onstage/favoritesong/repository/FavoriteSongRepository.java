package org.onstage.favoritesong.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.favoritesong.model.FavoriteSong;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoriteSongRepository {
    private final FavoriteSongRepo favoriteSongRepo;
    private final MongoTemplate mongoTemplate;

    public void save(FavoriteSong favoriteSong) {
        favoriteSongRepo.save(favoriteSong);
    }

    public List<String> getAllByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));

        return mongoTemplate.find(query, FavoriteSong.class)
                .stream()
                .map(FavoriteSong::songId)
                .toList();
    }


    public void removeFavoriteSong(String songId, String userId) {
        Query query = new Query(Criteria.where("songId").is(songId).and("userId").is(userId));
        mongoTemplate.remove(query, FavoriteSong.class);
    }

    public FavoriteSong findBySongIdAndUserId(String songId, String userId) {
        Query query = new Query(Criteria.where("songId").is(songId).and("userId").is(userId));
        return mongoTemplate.findOne(query, FavoriteSong.class);
    }
}

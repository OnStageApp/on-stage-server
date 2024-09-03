package org.onstage.favoritesong.repository;

import org.onstage.favoritesong.model.FavoriteSong;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteSongRepo extends MongoRepository<FavoriteSong, String> {
}

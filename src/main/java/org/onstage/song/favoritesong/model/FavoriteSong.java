package org.onstage.song.favoritesong.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record FavoriteSong(String userId, String songId) {
}

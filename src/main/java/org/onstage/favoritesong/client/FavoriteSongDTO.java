package org.onstage.favoritesong.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record FavoriteSongDTO(String userId, String songId) {
}

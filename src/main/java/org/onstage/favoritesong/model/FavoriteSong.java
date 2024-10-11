package org.onstage.favoritesong.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@FieldNameConstants
public record FavoriteSong(String userId, String songId) {
}

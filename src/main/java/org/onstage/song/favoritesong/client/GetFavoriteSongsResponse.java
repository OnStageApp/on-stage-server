package org.onstage.song.favoritesong.client;

import lombok.Builder;
import org.onstage.song.client.SongOverview;

import java.util.List;

@Builder
public record GetFavoriteSongsResponse(List<SongOverview> favoriteSongs) {
}

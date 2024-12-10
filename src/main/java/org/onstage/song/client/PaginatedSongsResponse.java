package org.onstage.song.client;

import lombok.Builder;
import org.onstage.song.model.Song;

import java.util.List;

@Builder
public record PaginatedSongsResponse(List<Song> songs , boolean hasMore) {
}

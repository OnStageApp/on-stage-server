package org.onstage.artist.client;

import lombok.Builder;
import org.onstage.artist.model.Artist;

import java.util.List;

@Builder
public record PaginatedArtistResponse(List<Artist> artists, boolean hasMore) {
}

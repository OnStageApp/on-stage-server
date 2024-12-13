package org.onstage.artist.client;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllArtistsResponse(List<ArtistDTO> artists, boolean hasMore) {
}

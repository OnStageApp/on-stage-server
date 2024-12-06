package org.onstage.artist.client;

import lombok.Builder;
import lombok.Data;

@Builder
public record GetArtistFilter(String search) {
}

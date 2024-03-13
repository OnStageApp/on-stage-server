package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.Artist;

@Builder(toBuilder = true)
public record SongOverview(
        String id,
        String title,
        Artist artist,
        String key,
        int tempo
) {
}

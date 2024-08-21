package org.onstage.song.client;

import lombok.Builder;
import org.onstage.artist.client.Artist;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Song(
        String id,
        String title,
        String lyrics,
        Integer tempo,
        String key,
        Artist artist
) {
}

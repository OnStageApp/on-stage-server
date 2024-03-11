package org.onstage.song.client;

import org.onstage.artist.client.Artist;

import java.time.LocalDateTime;

public record Song(
        String id,
        String title,
        String lyrics,
        String tab,
        String key,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Artist artist
) {
}

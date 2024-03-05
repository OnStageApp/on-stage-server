package org.onstage.song.client;

public record Song (
        String id,
        String title,
        String lyrics,
        String tab,
        String key,
        String createdAt,
        String updatedAt,
        String artist
) {
}

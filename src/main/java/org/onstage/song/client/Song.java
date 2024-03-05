package org.onstage.song.client;

import java.time.LocalDateTime;

public record Song (
        String id,
        String title,
        String lyrics,
        String tab,
        String key,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String artist
) {
}

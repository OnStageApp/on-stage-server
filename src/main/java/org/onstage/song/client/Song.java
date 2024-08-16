package org.onstage.song.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Song(
        String id,
        String title,
        String lyrics,
        Integer tempo,
        String key,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String artistId
) {
}

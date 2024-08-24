package org.onstage.event.client;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record EventOverview(
        String id,
        String name,
        LocalDateTime dateTime,
        List<String> stagersPhotos
) {
}

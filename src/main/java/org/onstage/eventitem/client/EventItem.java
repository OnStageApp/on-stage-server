package org.onstage.eventitem.client;

import lombok.Builder;
import org.onstage.enums.EventItemType;
import org.onstage.song.client.SongOverview;

@Builder(toBuilder = true)
public record EventItem(
        String id,
        String name,
        Integer index,
        EventItemType eventType,
        SongOverview song,
        String eventId
) {
}

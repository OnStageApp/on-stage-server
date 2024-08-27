package org.onstage.event.client;

import lombok.Builder;
import org.onstage.enums.EventSearchType;

@Builder
public record GetAllEventsRequest(
        EventSearchType eventSearchType,
        String searchValue
) {
}

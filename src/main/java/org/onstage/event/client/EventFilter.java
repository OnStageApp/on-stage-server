package org.onstage.event.client;

import lombok.Builder;

import java.util.List;

@Builder
public record EventFilter(
        List<String> stagerIds
) {
}

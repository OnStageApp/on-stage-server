package org.onstage.stager.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CreateStagerRequest(
        String eventId,
        List<String> teamMemberIds
) {
}

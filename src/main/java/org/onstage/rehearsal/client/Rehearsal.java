package org.onstage.rehearsal.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record Rehearsal(
        String id,
        String name,
        LocalDateTime dateTime,
        String location,
        String eventId) {

}

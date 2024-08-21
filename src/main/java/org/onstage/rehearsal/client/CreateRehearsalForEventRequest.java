package org.onstage.rehearsal.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record CreateRehearsalForEventRequest(
        String name,
        String location,
        LocalDateTime dateTime
){
}

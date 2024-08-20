package org.onstage.rehearsal.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record CreateRehearsalRequest (
        String name,
        String location,
        LocalDateTime dateTime
){
}

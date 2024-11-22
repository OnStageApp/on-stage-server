package org.onstage.song.client;

import lombok.Builder;

@Builder
public record TempoRange(Integer min, Integer max) {
}

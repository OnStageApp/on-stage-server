package org.onstage.event.controller;

import lombok.Builder;

import java.util.List;

@Builder
public record EventFilter(
        List<String> stagerIds
) {
}

package org.onstage.team.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record TeamDTO(
        String id,
        String name,
        Integer membersCount,
        List<String> memberPhotoUrls
) {
}

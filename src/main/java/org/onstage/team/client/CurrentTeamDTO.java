package org.onstage.team.client;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CurrentTeamDTO(
        String id,
        String name,
        Integer membersCount,
        List<String> membersUserIds
) {
}

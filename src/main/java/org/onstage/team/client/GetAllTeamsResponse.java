package org.onstage.team.client;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllTeamsResponse(
        List<TeamDTO> teams,
        String currentTeamId
) {
}

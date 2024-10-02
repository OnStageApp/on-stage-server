package org.onstage.team.client;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllTeamsResponse(
        List<GetTeamResponse> teams,
        String currentTeamId
) {
}

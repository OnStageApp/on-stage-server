package org.onstage.team.model.mapper;

import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMapper {

    public TeamDTO toDto(Team entity) {
        return TeamDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .build();
    }

    public Team toEntity(TeamDTO request) {
        return Team.builder()
                .id(request.id())
                .name(request.name())
                .build();
    }

    public List<TeamDTO> toDtoList(List<Team> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}

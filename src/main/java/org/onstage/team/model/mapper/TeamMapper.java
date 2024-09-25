package org.onstage.team.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.team.client.CurrentTeamDTO;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.teammember.service.TeamMemberService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamMapper {
    private final TeamMemberService teamMemberService;

    public TeamDTO toDto(Team entity) {
        return TeamDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .membersCount(teamMemberService.countByTeamId(entity.id()))
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

    public CurrentTeamDTO toCurrentTeamDTO(Team team) {
        return CurrentTeamDTO.builder()
                .id(team.id())
                .name(team.name())
                .membersCount(teamMemberService.countByTeamId(team.id()))
                .membersUserIds(teamMemberService.getMemberWithPhotoIds(team.id()))
                .build();
    }
}

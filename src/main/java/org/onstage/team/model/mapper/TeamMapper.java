package org.onstage.team.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.team.client.CurrentTeamDTO;
import org.onstage.team.client.GetTeamResponse;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.teammember.service.TeamMemberService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamMapper {
    private final TeamMemberService teamMemberService;

    public TeamDTO toDto(Team entity) {
        return TeamDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .membersCount(teamMemberService.countByTeamId(entity.getId()))
                .build();
    }

    public Team toEntity(TeamDTO request, String userId) {
        return Team.builder()
                .id(request.id())
                .name(request.name())
                .leaderId(userId)
                .build();
    }

    public List<GetTeamResponse> toDtoList(List<Team> entities, String userId) {
        return entities.stream()
                .map(team -> toGetTeamResponse(team, userId))
                .toList();
    }

    public CurrentTeamDTO toCurrentTeamDTO(Team team) {
        return CurrentTeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .membersCount(teamMemberService.countByTeamId(team.getId()))
                .membersUserIds(teamMemberService.getMemberWithPhotoIds(team.getId()))
                .build();
    }

    public GetTeamResponse toGetTeamResponse(Team team, String userId) {
        return GetTeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .membersCount(teamMemberService.countByTeamId(team.getId()))
                .role(StringUtils.capitalize(teamMemberService.getRole(team, userId).getValue()))
                .build();
    }
}

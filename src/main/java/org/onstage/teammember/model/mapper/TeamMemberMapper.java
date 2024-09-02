package org.onstage.teammember.model.mapper;

import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMemberMapper {

    public TeamMemberDTO toDto(TeamMember entity) {
        return TeamMemberDTO.builder()
                .id(entity.id())
                .teamId(entity.teamId())
                .userId(entity.userId())
                .memberRight(entity.memberRight())
                .build();
    }

    public TeamMember toEntity(TeamMemberDTO request) {
        return TeamMember.builder()
                .id(request.id())
                .teamId(request.teamId())
                .userId(request.userId())
                .memberRight(request.memberRight())
                .build();
    }

    public List<TeamMemberDTO> toDtoList(List<TeamMember> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}

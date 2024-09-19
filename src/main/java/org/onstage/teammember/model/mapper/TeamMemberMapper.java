package org.onstage.teammember.model.mapper;

import org.onstage.teammember.client.GetTeamMembersResponse;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMemberMapper {

    public TeamMemberDTO toDto(TeamMember entity) {
        return TeamMemberDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .userId(entity.userId())
                .teamId(entity.teamId())
                .role(entity.role())
                .build();
    }

    public TeamMember toEntity(TeamMemberDTO request) {
        return TeamMember.builder()
                .id(request.id())
                .name(request.name())
                .userId(request.userId())
                .teamId(request.teamId())
                .role(request.role())
                .build();
    }

    public List<TeamMemberDTO> toDtoList(List<TeamMember> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public GetTeamMembersResponse toGetTeamMemberResponse(TeamMember response) {
        return GetTeamMembersResponse.builder()
                .id(response.id())
                .name(response.name())
                .userId(response.userId())
                .teamId(response.teamId())
                .role(response.role())
                .build();
    }

    public List<GetTeamMembersResponse> toGetTeamMembersResponse(List<TeamMember> responses) {
        return responses.stream()
                .map(this::toGetTeamMemberResponse)
                .toList();
    }
}

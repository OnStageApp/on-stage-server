package org.onstage.teammember.model.mapper;

import org.onstage.teammember.client.GetTeamMembersResponse;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.client.TeamMemberIdWithPhoto;
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
                .inviteStatus(entity.inviteStatus())
                .build();
    }

    public TeamMember toEntity(TeamMemberDTO request) {
        return TeamMember.builder()
                .id(request.id())
                .name(request.name())
                .userId(request.userId())
                .teamId(request.teamId())
                .role(request.role())
                .inviteStatus(request.inviteStatus())
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
                .inviteStatus(response.inviteStatus())
                .build();
    }

    public List<TeamMemberIdWithPhoto> toTeamMemberIdWithPhoto(List<TeamMember> responses) {
        return responses.stream()
                .map(response -> TeamMemberIdWithPhoto.builder()
                        .id(response.id())
                        .userId(response.userId())
                        .photoUrl(null)
                        .build())
                .toList();
    }

    public List<GetTeamMembersResponse> toGetTeamMembersResponse(List<TeamMember> responses) {
        return responses.stream()
                .map(this::toGetTeamMemberResponse)
                .toList();
    }
}

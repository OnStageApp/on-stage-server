package org.onstage.teammember.model.mapper;

import org.onstage.teammember.client.GetTeamMemberPhoto;
import org.onstage.teammember.client.GetTeamMembersResponse;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMemberMapper {

    public TeamMemberDTO toDto(TeamMember entity) {
        return TeamMemberDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .userId(entity.getUserId())
                .teamId(entity.getTeamId())
                .role(entity.getRole())
                .inviteStatus(entity.getInviteStatus())
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
                .id(response.getId())
                .name(response.getName())
                .userId(response.getUserId())
                .teamId(response.getTeamId())
                .role(response.getRole())
                .inviteStatus(response.getInviteStatus())
                .build();
    }

    public GetTeamMemberPhoto toTeamMemberPhoto(TeamMember response) {
        return GetTeamMemberPhoto.builder()
                .id(response.getId())
                .userId(response.getUserId())
                .photoUrl(null)
                .build();
    }

    public List<GetTeamMemberPhoto> toTeamMemberPhotos(List<TeamMember> responses) {
        return responses.stream()
                .map(this::toTeamMemberPhoto)
                .toList();
    }

    public List<GetTeamMembersResponse> toGetTeamMembersResponse(List<TeamMember> responses) {
        return responses.stream()
                .map(this::toGetTeamMemberResponse)
                .toList();
    }
}

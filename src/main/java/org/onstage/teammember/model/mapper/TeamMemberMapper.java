package org.onstage.teammember.model.mapper;

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
                .role(entity.role())
                .build();
    }

    public TeamMember toEntity(TeamMemberDTO request) {
        return TeamMember.builder()
                .id(request.id())
                .name(request.name())
                .userId(request.userId())
                .role(request.role())
                .build();
    }

    public List<TeamMemberDTO> toDtoList(List<TeamMember> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}

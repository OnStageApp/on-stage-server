package org.onstage.stager.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StagerMapper {
    private final UserService userService;

    public StagerDTO toDto(Stager entity) {
        User user = userService.getById(entity.getUserId());
        return StagerDTO.builder()
                .id(entity.getId())
                .eventId(entity.getEventId())
                .teamMemberId(entity.getTeamMemberId())
                .userId(entity.getUserId())
                .name(user.getName())
                .participationStatus(entity.getParticipationStatus())
                .build();
    }

    public Stager toEntity(StagerDTO dto) {
        return Stager.builder()
                .id(dto.id())
                .eventId(dto.eventId())
                .teamMemberId(dto.teamMemberId())
                .userId(dto.userId())
                .participationStatus(dto.participationStatus())
                .build();
    }

    public List<StagerDTO> toDtoList(List<Stager> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
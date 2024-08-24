package org.onstage.stager.model.mapper;

import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StagerMapper {
    public StagerDTO toDto(Stager entity) {
        return StagerDTO.builder()
                .id(entity.id())
                .eventId(entity.eventId())
                .userId(entity.userId())
                .name(entity.name())
                .profilePicture(entity.profilePicture())
                .participationStatus(entity.participationStatus())
                .build();
    }

    public Stager toEntity(StagerDTO dto) {
        return Stager.builder()
                .id(dto.id())
                .eventId(dto.eventId())
                .userId(dto.userId())
                .name(dto.name())
                .profilePicture(dto.profilePicture())
                .participationStatus(dto.participationStatus())
                .build();
    }

    public List<StagerDTO> toDtoList(List<Stager> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
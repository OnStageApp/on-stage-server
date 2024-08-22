package org.onstage.stager.model.mapper;

import org.onstage.stager.client.Stager;
import org.onstage.stager.model.StagerEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StagerMapper {
    public Stager toDto(StagerEntity entity) {
        return Stager.builder()
                .id(entity.id())
                .eventId(entity.eventId())
                .userId(entity.userId())
                .name(entity.name())
                .profilePicture(entity.profilePicture())
                .participationStatus(entity.participationStatus())
                .build();
    }

    public StagerEntity toEntity(Stager dto) {
        return StagerEntity.builder()
                .id(dto.id())
                .eventId(dto.eventId())
                .userId(dto.userId())
                .name(dto.name())
                .profilePicture(dto.profilePicture())
                .participationStatus(dto.participationStatus())
                .build();
    }

    public List<Stager> toDtoList(List<StagerEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
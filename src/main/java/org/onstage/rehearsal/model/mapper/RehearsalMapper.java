package org.onstage.rehearsal.model.mapper;

import org.onstage.rehearsal.client.Rehearsal;
import org.onstage.rehearsal.model.RehearsalEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RehearsalMapper {
    public Rehearsal toDto(RehearsalEntity entity) {
        return Rehearsal.builder()
                .id(entity.id())
                .name(entity.name())
                .dateTime(entity.dateTime())
                .location(entity.location())
                .eventId(entity.eventId())
                .build();
    }

    public RehearsalEntity toEntity(Rehearsal request) {
        return RehearsalEntity.builder()
                .id(request.id())
                .name(request.name())
                .dateTime(request.dateTime())
                .location(request.location())
                .eventId(request.eventId())
                .build();
    }

    public List<Rehearsal> toDtoList(List<RehearsalEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public List<RehearsalEntity> toEntityList(List<Rehearsal> requests) {
        return requests.stream()
                .map(this::toEntity)
                .toList();
    }
}

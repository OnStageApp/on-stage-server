package org.onstage.rehearsal.model.mapper;

import org.onstage.rehearsal.client.RehearsalDTO;
import org.onstage.rehearsal.model.Rehearsal;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RehearsalMapper {
    public RehearsalDTO toDto(Rehearsal entity) {
        return RehearsalDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dateTime(entity.getDateTime())
                .location(entity.getLocation())
                .eventId(entity.getEventId())
                .build();
    }

    public Rehearsal toEntity(RehearsalDTO request) {
        return Rehearsal.builder()
                .id(request.id())
                .name(request.name())
                .dateTime(request.dateTime())
                .location(request.location())
                .eventId(request.eventId())
                .build();
    }

    public List<RehearsalDTO> toDtoList(List<Rehearsal> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

}

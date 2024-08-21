package org.onstage.rehearsal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.client.Rehearsal;
import org.onstage.rehearsal.model.RehearsalEntity;
import org.onstage.rehearsal.repository.RehearsalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RehearsalService {
    private final RehearsalRepository rehearsalRepository;

    public RehearsalEntity getById(String id) {
        return rehearsalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rehearsal with id:%s was not found".formatted(id)));
    }

    public List<RehearsalEntity> getAll(String eventId) {
        return rehearsalRepository.getAllByEventId(eventId);
    }

    public RehearsalEntity save(RehearsalEntity rehearsal) {
        RehearsalEntity savedRehearsal = rehearsalRepository.save(rehearsal);
        log.info("Rehearsal {} has been saved", rehearsal.id());
        return savedRehearsal;
    }

    public String delete(String id) {
        return rehearsalRepository.delete(id);
    }

    public RehearsalEntity update(String id, Rehearsal request) {
        RehearsalEntity existingRehearsal = getById(id);
        RehearsalEntity updatedRehearsal = updateRehearsalFromDTO(existingRehearsal, request);
        return rehearsalRepository.save(updatedRehearsal);
    }

    private RehearsalEntity updateRehearsalFromDTO(RehearsalEntity existingRehearsal, Rehearsal request) {
        return RehearsalEntity.builder()
                .id(existingRehearsal.id())
                .name(request.name() == null ? existingRehearsal.name() : request.name())
                .dateTime(request.dateTime() == null ? existingRehearsal.dateTime() : request.dateTime())
                .location(request.location() == null ? existingRehearsal.location() : request.location())
                .eventId(existingRehearsal.eventId())
                .build();
    }


    public void createRehearsalsForEvent(String eventId, List<CreateRehearsalForEventRequest> rehearsals) {
        rehearsals.forEach(rehearsal -> save(RehearsalEntity.builder()
                .name(rehearsal.name())
                .dateTime(rehearsal.dateTime())
                .location(rehearsal.location())
                .eventId(eventId)
                .build()));
    }
}

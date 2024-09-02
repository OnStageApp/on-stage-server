package org.onstage.rehearsal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.client.RehearsalDTO;
import org.onstage.rehearsal.model.Rehearsal;
import org.onstage.rehearsal.repository.RehearsalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RehearsalService {
    private final RehearsalRepository rehearsalRepository;

    public Rehearsal getById(String id) {
       return rehearsalRepository.getById(id);
    }

    public List<Rehearsal> getAll(String eventId) {
        return rehearsalRepository.getAllByEventId(eventId);
    }

    public Rehearsal save(Rehearsal rehearsal) {
        Rehearsal savedRehearsal = rehearsalRepository.save(rehearsal);
        log.info("Rehearsal {} has been saved", rehearsal.id());
        return savedRehearsal;
    }

    public String delete(String id) {
        log.info("Deleting rehearsal {}", id);
        return rehearsalRepository.delete(id);
    }

    public Rehearsal update(Rehearsal existingRehearsal, RehearsalDTO request) {
        log.info("Updating rehearsal {} with request {}", existingRehearsal.id(), request);
        Rehearsal updatedRehearsal = updateRehearsalFromDTO(existingRehearsal, request);
        return rehearsalRepository.save(updatedRehearsal);
    }

    private Rehearsal updateRehearsalFromDTO(Rehearsal existingRehearsal, RehearsalDTO request) {
        return Rehearsal.builder()
                .id(existingRehearsal.id())
                .name(request.name() == null ? existingRehearsal.name() : request.name())
                .dateTime(request.dateTime() == null ? existingRehearsal.dateTime() : request.dateTime())
                .location(request.location() == null ? existingRehearsal.location() : request.location())
                .eventId(existingRehearsal.eventId())
                .build();
    }


    public void createRehearsalsForEvent(String eventId, List<CreateRehearsalForEventRequest> rehearsals) {
        rehearsals.forEach(rehearsal -> save(Rehearsal.builder()
                .name(rehearsal.name())
                .dateTime(rehearsal.dateTime())
                .location(rehearsal.location())
                .eventId(eventId)
                .build()));
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all rehearsals for event {}", eventId);
        rehearsalRepository.deleteAllByEventId(eventId);
    }
}

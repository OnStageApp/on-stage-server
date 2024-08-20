package org.onstage.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.client.IEvent;
import org.onstage.event.model.EventEntity;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.rehearsal.client.CreateRehearsalRequest;
import org.onstage.rehearsal.service.RehearsalService;
import org.onstage.stager.service.StagerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository repository;
    private final ObjectMapper objectMapper;
    private final StagerService stagerService;
    private final RehearsalService rehearsalService;

    public EventEntity getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:%s was not found".formatted(id)));
    }

    public EventEntity create(EventEntity event, List<String> userIds, List<CreateRehearsalRequest> rehearsals) {
        EventEntity savedEvent = repository.save(event);
        stagerService.createStagersForEvent(savedEvent.id(), userIds);
        rehearsalService.createRehearsalsForEvent(savedEvent.id(), rehearsals);
        log.info("Event {} has been saved", savedEvent.id());
        return savedEvent;
    }

    public String delete(String id) {
        return repository.delete(id);
    }

    public List<IEvent> getAll(final String search) {
        return repository.getAll(search);
    }

    public List<IEvent> getAllByRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Events by range: " + startDate + " - " + endDate);
        return repository.getAllByRange(startDate, endDate);
    }

    public EventEntity patch(String id, JsonPatch jsonPatch) {
        return repository.save(applyPatchToEvent(getById(id), jsonPatch));
    }

    @SneakyThrows
    private EventEntity applyPatchToEvent(EventEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, EventEntity.class);
    }
}

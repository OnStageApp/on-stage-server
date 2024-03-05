package org.onstage.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.model.EventEntity;
import org.onstage.event.repository.EventItemRepository;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository repository;
    private final EventItemRepository eventItemRepository;
    private final ObjectMapper objectMapper;

    public EventEntity getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:%s was not found".formatted(id)));
    }

    public EventEntity create(EventEntity event) {
        EventEntity savedEvent = repository.save(event);
        log.info("Event has been saved | {}", event);
        return savedEvent;
    }

    public String delete(String id) {
        return repository.delete(id);
    }

    public List<EventEntity> getAll(final String search) {
        return repository.getAll(search);
    }

    public List<EventEntity> getAllByRange(LocalDateTime startDate, LocalDateTime endDate) {
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

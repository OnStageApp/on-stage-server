package org.onstage.rehearsal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.rehearsal.model.RehearsalEntity;
import org.onstage.rehearsal.repository.RehearsalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RehearsalService {
    private final RehearsalRepository rehearsalRepository;
    private final ObjectMapper objectMapper;

    public RehearsalEntity getById(String id) {
        return rehearsalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rehearsal with id:%s was not found".formatted(id)));
    }

    public List<RehearsalEntity> getAll(String eventId) {
        return rehearsalRepository.getAllByEventId(eventId);
    }

    public RehearsalEntity create(RehearsalEntity rehearsal) {
        RehearsalEntity savedRehearsal = rehearsalRepository.save(rehearsal);
        log.info("Rehearsal has been saved | {}", rehearsal);
        return savedRehearsal;
    }

    public String delete(String id) {
        return rehearsalRepository.delete(id);
    }

    public RehearsalEntity patch(String id, JsonPatch jsonPatch) {
        return rehearsalRepository.save(applyPatchToEvent(getById(id), jsonPatch));
    }

    @SneakyThrows
    private RehearsalEntity applyPatchToEvent(RehearsalEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, RehearsalEntity.class);
    }
}

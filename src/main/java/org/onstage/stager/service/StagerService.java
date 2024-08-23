package org.onstage.stager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.stager.client.Stager;
import org.onstage.stager.model.StagerEntity;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.model.UserEntity;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagerService {
    private final StagerRepository stagerRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public StagerEntity getById(String id) {
        return stagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stager with id:%s was not found".formatted(id)));
    }

    public List<StagerEntity> getAll(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public void createStagersForEvent(String eventId, List<String> userIds) {
        userIds.forEach(userId -> create(eventId, userId));
    }

    public StagerEntity create(String eventId, String userId) {
        UserEntity user = userService.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User with id:%s was not found".formatted(userId));
        }
        log.info("Creating stager for event {} and user {}", eventId, userId);
        return stagerRepository.createStager(eventId, user);

    }

    public String remove(String stagerId) {
        log.info("Removing stager with id {}", stagerId);
        stagerRepository.removeStager(stagerId);
        return stagerId;
    }

    public StagerEntity patch(String id, JsonPatch jsonPatch) {
        return stagerRepository.save(applyPatchToEvent(getById(id), jsonPatch));
    }

    @SneakyThrows
    private StagerEntity applyPatchToEvent(StagerEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, StagerEntity.class);
    }
}

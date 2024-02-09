package org.onstage.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.common.exceptions.ResourceNotFound;
import org.onstage.user.client.User;
import org.onstage.user.model.UserEntity;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public UserEntity getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("No user with id:%s was found".formatted(id)));
    }

    public UserEntity create(User user) {
        return userRepository.create(user);
    }

    public UserEntity patch(String id, JsonPatch jsonPatch) {
        return userRepository.save(applyPatchToUser(getById(id), jsonPatch));
    }

    @SneakyThrows
    private UserEntity applyPatchToUser(UserEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, UserEntity.class);
    }
}

package org.onstage.user.service;

import lombok.RequiredArgsConstructor;
import org.onstage.common.exceptions.ResourceNotFound;
import org.onstage.stager.model.StagerEntity;
import org.onstage.stager.repository.StagerRepo;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.client.User;
import org.onstage.user.model.UserEntity;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StagerRepository stagerRepository;

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public List<UserEntity> getAllUninvitedUsers(String eventId) {
        final List<StagerEntity> stagerEntities = stagerRepository.getAllByEventId(eventId);
        final List<UserEntity> userEntities = userRepository.findAll();

        return userEntities.stream()
                .filter(userEntity -> stagerEntities.stream()
                        .noneMatch(stagerEntity -> stagerEntity.userId().equals(userEntity.id())))
                .toList();
    }

    public UserEntity getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("No user with id:%s was found".formatted(id)));
    }

    public UserEntity save(User user) {
        return userRepository.save(user);
    }

    public UserEntity update(String id, User request) {
        UserEntity existingUser = getById(id);
        UserEntity updatedUser = updateUserFromDTO(existingUser, request);
        return userRepository.save(updatedUser);
    }

    private UserEntity updateUserFromDTO(UserEntity existingUser, User request) {
        return UserEntity.builder()
                .id(existingUser.id())
                .email(request.email() == null ? existingUser.email() : request.email())
                .name(request.name() == null ? existingUser.name() : request.name())
                .role(request.role() == null ? existingUser.role() : request.role())
                .build();
    }
}

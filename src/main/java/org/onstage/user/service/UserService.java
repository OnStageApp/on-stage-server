package org.onstage.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StagerRepository stagerRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllUninvitedUsers(String eventId) {
        final List<Stager> stagerEntities = stagerRepository.getAllByEventId(eventId);
        final List<User> userEntities = userRepository.findAll();

        return userEntities.stream()
                .filter(userEntity -> stagerEntities.stream()
                        .noneMatch(stagerEntity -> stagerEntity.userId().equals(userEntity.id())))
                .toList();
    }

    public User getById(String id) {
        return userRepository.getById(id);
    }

    public User save(User user) {
        User savedUser = userRepository.save(user);
        log.info("User {} has been saved", savedUser.id());
        return savedUser;
    }

    public User update(User existingUser, UserDTO request) {
        log.info("Updating user {} with request {}", existingUser.id(), request);
        User updatedUser = updateUserFromDTO(existingUser, request);
        return userRepository.save(updatedUser);
    }

    private User updateUserFromDTO(User existingUser, UserDTO request) {
        return User.builder()
                .id(existingUser.id())
                .email(request.email() == null ? existingUser.email() : request.email())
                .name(request.name() == null ? existingUser.name() : request.name())
                .role(request.role() == null ? existingUser.role() : request.role())
                .build();
    }
}

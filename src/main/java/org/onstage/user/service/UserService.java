package org.onstage.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.amazon.AmazonS3Service;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StagerRepository stagerRepository;
    private final AmazonS3Service amazonS3Service;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllUninvitedUsers(String eventId) {
        final List<Stager> stagers = stagerRepository.getAllByEventId(eventId);
        final List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> stagers.stream()
                        .noneMatch(stager -> stager.userId().equals(user.id())))
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

    private String getUserImageKey(String userId) {
        return "user/".concat(userId);
    }

    public void uploadUserPhoto(String id, byte[] image) {
        log.info("Uploading image for user {}", id);
        LocalDateTime now = image == null ? null : LocalDateTime.now();
        String key = getUserImageKey(id);
        amazonS3Service.putObject(image, key);
        log.info("Update image timestamp to {} for artist {}", now, id);
        userRepository.updateImageTimestamp(id, now);
    }

    public byte[] getUserPhoto(String id) {
        log.info("Getting image for user {}", id);
        String key = getUserImageKey(id);
        return amazonS3Service.getObject(key);
    }
}

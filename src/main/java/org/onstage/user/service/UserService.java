package org.onstage.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.amazon.AmazonS3Service;
import org.onstage.exceptions.BadRequestException;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AmazonS3Service amazonS3Service;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(BadRequestException::userNotFound);
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

    public void uploadUserPhoto(String id, byte[] image, String contentType) {
        log.info("Uploading image for user {}", id);
        LocalDateTime now = image == null ? null : LocalDateTime.now();
        String key = getUserImageKey(id);
        amazonS3Service.putObject(image, key, contentType);
        log.info("Update image timestamp to {} for artist {}", now, id);
        userRepository.updateImageTimestamp(id, now);
    }

    public byte[] getUserPhoto(String id) {
        log.info("Getting image for user {}", id);
        String key = getUserImageKey(id);
        return amazonS3Service.getObject(key);
    }

    public byte[] getUserThumbnail(String id) {
        log.info("Getting thumbnail for user {}", id);
        String key = getUserImageKey(id);
        return amazonS3Service.getThumbnail(key);
    }

    public List<byte[]> getRandomUserIdsWithPhotos(String eventId, int limit) {
        List<String> userIds = userRepository.getRandomUserIdsWithPhotos(eventId, limit);
        return userIds.stream()
                .map(this::getUserThumbnail)
                .toList();
    }

    public void setCurrentTeam(String teamId, String userId) {
        User user = getById(userId);
        save(user.toBuilder().currentTeamId(teamId).build());
    }
}

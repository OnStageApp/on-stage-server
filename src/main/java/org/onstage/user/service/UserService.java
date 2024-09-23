package org.onstage.user.service;

import com.amazonaws.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.amazon.AmazonS3Service;
import org.onstage.exceptions.BadRequestException;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.onstage.usersettings.service.UserSettingsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AmazonS3Service amazonS3Service;
    private final TeamMemberRepository teamMemberRepository;
    private final UserSettingsService userSettingsService;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(BadRequestException::userNotFound);
    }

    public User save(User user) {
        User savedUser = userRepository.save(user);
        log.info("User {} has been saved", savedUser.id());
        userSettingsService.createDefaultSettings(savedUser.id());
        return savedUser;
    }

    public User update(User existingUser, UserDTO request) {
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



    public void setCurrentTeam(String teamId, String userId) {
        User user = getById(userId);
        save(user.toBuilder().currentTeamId(teamId).build());
    }

    public String getThumbnailPresignedUrl(String userId) {
        User user = getById(userId);
        if (user.imageTimestamp() == null) {
            return null;
        }

        return amazonS3Service.generateUserThumbnailPresignedUrl(userId, HttpMethod.GET).toString();
    }

    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }


    public void uploadPhoto(String userId, byte[] image, String contentType) {
        log.info("Uploading image for user {}", userId);
        LocalDateTime now = image == null ? null : LocalDateTime.now();
        amazonS3Service.putObject(image, userId, contentType);
        log.info("Update image timestamp to {} for user {}", now, userId);
        userRepository.updateImageTimestamp(userId, now);
    }
}

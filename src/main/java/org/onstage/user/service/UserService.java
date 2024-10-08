package org.onstage.user.service;

import com.amazonaws.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.onstage.amazon.AmazonS3Service;
import org.onstage.exceptions.BadRequestException;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.team.repository.TeamRepository;
import org.onstage.teammember.model.TeamMember;
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
    private final StagerRepository stagerRepository;
    private final TeamRepository teamRepository;
    private final FirebaseAuth firebaseAuth;


    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(BadRequestException::userNotFound);
    }

    public User create(User user) {
        User savedUser = userRepository.save(user);
        log.info("User {} has been saved", savedUser.id());
        userSettingsService.createDefaultSettings(savedUser.id());
        return savedUser;
    }

    public User update(User existingUser, UserDTO request) {
        if (!Strings.isEmpty(request.name())) {
            List<TeamMember> teamMembers = teamMemberRepository.getAllByUserId(existingUser.id());
            for (TeamMember teamMember : teamMembers) {
                teamMemberRepository.save(teamMember.toBuilder().name(request.name()).build());
            }
        }
        User updatedUser = updateUserFromDTO(existingUser, request);
        return userRepository.save(updatedUser);
    }

    private User updateUserFromDTO(User existingUser, UserDTO request) {
        return existingUser.toBuilder()
                .email(request.email() == null ? existingUser.email() : request.email())
                .name(request.name() == null ? existingUser.name() : request.name())
                .role(request.role() == null ? existingUser.role() : request.role())
                .build();
    }


    public void setCurrentTeam(String teamId, String userId) {
        User user = getById(userId);
        userRepository.save(user.toBuilder().currentTeamId(teamId).build());
    }

    public String getPresignedUrl(String userId, boolean isThumbnail) {
        User user = getById(userId);
        if (user.imageTimestamp() == null) {
            return null;
        }
        if (isThumbnail)
            return amazonS3Service.generateUserThumbnailPresignedUrl(userId, HttpMethod.GET).toString();
        return amazonS3Service.generateUserProfilePresignedUrl(userId, HttpMethod.GET).toString();
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

    public List<String> getStagersWithPhoto(String eventId) {
        return userRepository.getStagersWithPhoto(eventId);
    }

    public void delete(String userId) {
        try {
            stagerRepository.deleteAllByUserId(userId);
            teamMemberRepository.deleteAllByUserId(userId);
            teamRepository.deleteAllByLeaderId(userId);
            // delete events where user is the team leader
            firebaseAuth.deleteUser(userId);
            userRepository.deleteById(userId);
        } catch (FirebaseAuthException e) {
            log.error("Error deleting user", e);
            throw BadRequestException.invalidRequest();
        }
    }
}

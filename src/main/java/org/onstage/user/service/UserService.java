package org.onstage.user.service;

import com.amazonaws.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.amazon.AmazonS3Service;
import org.onstage.exceptions.BadRequestException;
import org.onstage.stager.client.StagerDTO;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.teammember.service.TeamMemberService;
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
    private final AmazonS3Service amazonS3Service;
    private final TeamMemberRepository teamMemberRepository;

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

    public List<String> getStagersPhotos(String eventId) {
        List<String> userIds = userRepository.getStagersWithPhoto(eventId);
        return userIds.stream()
                .map(user -> generatePresignedUrl(user, HttpMethod.GET))
                .toList();
    }

    public List<String> getMembersPhotos(String teamId) {
        List<String> userIds = userRepository.getMembersWithPhoto(teamId);
        return userIds.stream()
                .map(user -> generatePresignedUrl(user, HttpMethod.GET))
                .toList();
    }


    public void setCurrentTeam(String teamId, String userId) {
        User user = getById(userId);
        save(user.toBuilder().currentTeamId(teamId).build());
    }

    public String generatePresignedUrl(String userId, HttpMethod httpMethod) {
        if(httpMethod == HttpMethod.PUT) {
            userRepository.updateImageTimestamp(userId, LocalDateTime.now());
        }
        if(httpMethod == HttpMethod.GET) {
            User user = getById(userId);
            if(user.imageTimestamp() == null) {
                return null;
            }
        }
        return amazonS3Service.generatePresignedUrl(getUserImageKey(userId), httpMethod).toString();
    }

    public String getStagerPhoto(StagerDTO stager) {
        TeamMember teamMember = teamMemberRepository.findById(stager.teamMemberId()).orElseThrow(BadRequestException::teamMemberNotFound);
        return generatePresignedUrl(teamMember.userId(), HttpMethod.GET);
    }

    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }
}

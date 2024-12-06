package org.onstage.user.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.UserDTO;
import org.onstage.user.client.UserProfileInfoDTO;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final UserService userService;
    public UserDTO toDto(User entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .imageTimestamp(entity.getImageTimestamp())
                .role(entity.getRole())
                .currentTeamId(entity.getCurrentTeamId())
                .position(entity.getPosition())
                .build();
    }

    public User toEntity(UserDTO request) {
        return User.builder()
                .id(request.id())
                .name(request.name())
                .email(request.email())
                .imageTimestamp(request.imageTimestamp())
                .role(request.role())
                .currentTeamId(request.currentTeamId())
                .position(request.position())
                .build();
    }

    public List<UserDTO> toDtoList(List<User> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public UserProfileInfoDTO toProfileInfoDTO(User entity) {
        return UserProfileInfoDTO.builder()
                .name(entity.getName())
                .email(entity.getEmail())
                .position(entity.getPosition())
                .photoUrl(userService.getPresignedUrl(entity.getId(), false))
                .build();
    }
}

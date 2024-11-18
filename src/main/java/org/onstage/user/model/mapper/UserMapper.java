package org.onstage.user.model.mapper;

import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component()
public class UserMapper {
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

    public List<User> toEntityList(List<UserDTO> requests) {
        return requests.stream().map(this::toEntity).toList();
    }
}

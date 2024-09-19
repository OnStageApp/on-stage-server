package org.onstage.user.model.mapper;

import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component()
public class UserMapper {
    public UserDTO toDto(User entity) {
        return UserDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .email(entity.email())
                .imageTimestamp(entity.imageTimestamp())
                .role(entity.role())
                .currentTeamId(entity.currentTeamId())
                .build();
    }

    public User toEntity(UserDTO request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .imageTimestamp(request.imageTimestamp())
                .role(request.role())
                .currentTeamId(request.currentTeamId())
                .build();
    }

    public List<UserDTO> toDtoList(List<User> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<User> toEntityList(List<UserDTO> requests) {
        return requests.stream().map(this::toEntity).toList();
    }
}

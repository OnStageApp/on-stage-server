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
                .profilePicture(entity.profilePicture())
                .build();
    }

    public User toEntity(UserDTO request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .profilePicture(request.profilePicture())
                .build();
    }

    public List<UserDTO> toDtoList(List<User> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<User> toEntityList(List<UserDTO> requests) {
        return requests.stream().map(this::toEntity).toList();
    }
}

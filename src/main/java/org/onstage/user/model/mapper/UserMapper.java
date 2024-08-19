package org.onstage.user.model.mapper;

import org.mapstruct.Mapper;
import org.onstage.user.client.User;
import org.onstage.user.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component()
public class UserMapper {
    public User toDto(UserEntity entity) {
        return User.builder().id(entity.id()).name(entity.name()).email(entity.email()).build();
    }

    public UserEntity toEntity(User request) {
        return UserEntity.builder().name(request.name()).email(request.email()).build();
    }

    public List<User> toDtoList(List<UserEntity> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<UserEntity> toEntityList(List<User> requests) {
        return requests.stream().map(this::toEntity).toList();
    }
}

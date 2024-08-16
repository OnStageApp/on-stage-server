package org.onstage.user.model.mapper;

import org.mapstruct.Mapper;
import org.onstage.user.client.User;
import org.onstage.user.model.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDto(UserEntity entity);

    UserEntity toEntity(User request);

    List<User> toDtoList(List<UserEntity> entities);

    List<UserEntity> toEntityList(List<User> requests);
}

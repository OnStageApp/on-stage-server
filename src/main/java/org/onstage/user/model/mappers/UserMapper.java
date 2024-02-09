package org.onstage.user.model.mappers;

import org.mapstruct.Mapper;
import org.onstage.user.client.User;
import org.onstage.user.model.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toApi(UserEntity entity);

    UserEntity toDb(User request);

    List<User> toApiList(List<UserEntity> entities);

    List<UserEntity> toDbList(List<User> requests);
}

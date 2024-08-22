package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.User;
import org.onstage.user.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
public class UserRepository {
    private final UserRepo userRepo;

    public List<UserEntity> findAll() {
        return userRepo.findAll();
    }

    public List<UserEntity> getAllUninvitedUsers(String eventId) {
        return userRepo.findAll();
    }

    public Optional<UserEntity> findById(String id) {
        return userRepo.findById(id);
    }

    public UserEntity save(User user) {
        return userRepo.save(UserEntity.builder()
                .id(randomUUID().toString())
                .name(user.name())
                .role(user.role())
                .build());
    }

    public UserEntity save(UserEntity user) {
        return userRepo.save(user);
    }
}

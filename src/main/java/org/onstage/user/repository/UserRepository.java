package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
public class UserRepository {
    private final UserRepo userRepo;

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public List<User> getAllUninvitedUsers(String eventId) {
        return userRepo.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepo.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public User save(UserDTO user) {
        return userRepo.save(User.builder()
                .id(randomUUID().toString())
                .name(user.name())
                .role(user.role())
                .build());
    }

    public User save(User user) {
        return userRepo.save(user);
    }
}

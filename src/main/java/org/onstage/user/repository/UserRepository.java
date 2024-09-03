package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
public class UserRepository {
    private final UserRepo userRepo;
    private final MongoTemplate mongoTemplate;

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

    public User save(User user) {
        return userRepo.save(user);
    }

    public User getById(String id) {
        Criteria criteria = Criteria.where(User.Fields.id).is(id);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, User.class);
    }
}

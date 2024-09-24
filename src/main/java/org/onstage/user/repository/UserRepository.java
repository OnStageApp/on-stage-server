package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.ParticipationStatus;
import org.onstage.stager.model.Stager;
import org.onstage.user.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.onstage.user.model.User.Fields.imageTimestamp;

@Component
@RequiredArgsConstructor
public class UserRepository {
    private final UserRepo userRepo;
    private final MongoTemplate mongoTemplate;

    public List<User> findAll() {
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

    public void updateImageTimestamp(String id, LocalDateTime now) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set(imageTimestamp, now);
        mongoTemplate.updateFirst(query, update, User.class);
    }


    public List<String> getStagersWithPhoto(String eventId) {
        Criteria stagerCriteria = Criteria.where(Stager.Fields.eventId).is(eventId)
                .and(Stager.Fields.participationStatus).is(ParticipationStatus.CONFIRMED);
        List<Stager> stagers = mongoTemplate.find(new Query(stagerCriteria), Stager.class);

        List<String> users = stagers.stream().map(Stager::userId).toList();

        Criteria userCriteria = Criteria.where(User.Fields.id).in(users)
                .and(imageTimestamp).ne(null);

        return mongoTemplate.find(new Query(userCriteria).limit(2), User.class).stream()
                .map(User::id)
                .collect(Collectors.toList());
    }

    public User getByEmail(String email) {
        Criteria criteria = Criteria.where(User.Fields.email).is(email);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, User.class);
    }
}

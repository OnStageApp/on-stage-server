package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.model.Stager;
import org.onstage.teammember.model.TeamMember;
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


    public List<String> getRandomUserIdsWithPhotos(String eventId, Integer limit) {
//        Criteria stagerCriteria = Criteria.where(Stager.Fields.eventId).is(eventId);
//        Query stagerQuery = new Query(stagerCriteria);
//        List<Stager> stagers = mongoTemplate.find(stagerQuery, Stager.class);
//
//        List<String> teamMembers = stagers.stream()
//                .map(Stager::teamMemberId)
//                .toList();
//
//        List<String> teamMemberIds = teamMembers.stream()
//                .map(TeamMember::userId)
//                .collect(Collectors.toList());
//
//        Criteria userCriteria = Criteria.where(User.Fields.id).in(teamMemberIds)
//                .and(imageTimestamp).ne(null);
//        Query userQuery = new Query(userCriteria);
//        userQuery.fields().include(User.Fields.id);
//        userQuery.limit(limit);
//
//        List<User> users = mongoTemplate.find(userQuery, User.class);
//
//        return users.stream()
//                .map(User::id)
//                .collect(Collectors.toList());
        return null;
    }
}

package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.ParticipationStatus;
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


    public List<String> getStagersWithPhoto(String eventId) {
        Criteria stagerCriteria = Criteria.where(Stager.Fields.eventId).is(eventId)
                .and(Stager.Fields.participationStatus).is(ParticipationStatus.CONFIRMED);
        Query stagerQuery = new Query(stagerCriteria);
        List<Stager> stagers = mongoTemplate.find(stagerQuery, Stager.class);

        List<TeamMember> teamMembers = mongoTemplate.find(Query.query(Criteria.where(TeamMember.Fields.id).in(stagers.stream().map(Stager::teamMemberId).toList())), TeamMember.class);

        List<String> userIds = teamMembers.stream()
                .map(TeamMember::userId)
                .collect(Collectors.toList());

        Criteria userCriteria = Criteria.where(User.Fields.id).in(userIds)
                .and(imageTimestamp).ne(null);
        Query userQuery = Query.query(userCriteria).limit(2);

        List<User> users = mongoTemplate.find(userQuery, User.class);

        return users.stream()
                .map(User::id)
                .collect(Collectors.toList());
    }

    public List<String> getMembersWithPhoto(String teamId) {
        List<TeamMember> teamMembers = mongoTemplate.find(Query.query(Criteria.where(TeamMember.Fields.teamId).is(teamId)), TeamMember.class);

        List<String> userIds = teamMembers.stream()
                .map(TeamMember::userId)
                .collect(Collectors.toList());

        Criteria userCriteria = Criteria.where(User.Fields.id).in(userIds)
                .and(imageTimestamp).ne(null);
        Query userQuery = Query.query(userCriteria).limit(2);

        List<User> users = mongoTemplate.find(userQuery, User.class);

        return users.stream()
                .map(User::id)
                .collect(Collectors.toList());
    }

    public User getByEmail(String email) {
        Criteria criteria = Criteria.where(User.Fields.email).is(email);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, User.class);
    }
}

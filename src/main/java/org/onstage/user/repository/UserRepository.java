package org.onstage.user.repository;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.ParticipationStatus;
import org.onstage.user.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
        Update update = new Update().set(User.Fields.imageTimestamp, now);
        mongoTemplate.updateFirst(query, update, User.class);
    }


    public List<String> getUserIdsWithPhotoFromEvent(String eventId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("imageTimestamp").ne(null)),
                Aggregation.lookup("stagers", "_id", "userId", "stagers"),
                Aggregation.unwind("stagers"),
                Aggregation.match(Criteria.where("stagers.eventId").is(eventId)
                        .and("stagers.participationStatus").is(ParticipationStatus.CONFIRMED)),
                Aggregation.limit(2),
                Aggregation.project("_id")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "users", Document.class);
        return results.getMappedResults().stream()
                .map(doc -> doc.getString("_id"))
                .collect(Collectors.toList());
    }

    public User getByEmail(String email) {
        Criteria criteria = Criteria.where(User.Fields.email).is(email);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, User.class);
    }

    public void deleteById(String userId) {
        userRepo.deleteById(userId);
    }

    public User findByIdOrTeamId(String appUserId) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where(User.Fields.id).is(appUserId),
                Criteria.where(User.Fields.currentTeamId).is(appUserId)
        );
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, User.class);
    }

    public List<String> getUserIdsWithPhotoFromTeam(String teamId) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("imageTimestamp").ne(null)),
            Aggregation.lookup("teamMembers", "_id", "userId", "teamMembers"),
            Aggregation.unwind("teamMembers"),
            Aggregation.match(Criteria.where("teamMembers.teamId").is(teamId)
                    .and("teamMembers.inviteStatus").is(MemberInviteStatus.CONFIRMED)),
            Aggregation.limit(2),
            Aggregation.project("_id")
    );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "users", Document.class);
        return results.getMappedResults().stream()
                .map(doc -> doc.getString("_id"))
                .collect(Collectors.toList());
    }

    public User getByUsername(String username) {
        Criteria criteria = Criteria.where(User.Fields.username).is(username);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, User.class);
    }
}

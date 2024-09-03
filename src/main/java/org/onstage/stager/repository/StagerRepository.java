package org.onstage.stager.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.model.Stager;
import org.onstage.user.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.onstage.enums.ParticipationStatus.PENDING;

@Component
@RequiredArgsConstructor
public class StagerRepository {
    private final StagerRepo stagerRepo;
    private final MongoTemplate mongoTemplate;

    public Stager getById(String id) {
        Criteria criteria = Criteria.where(Stager.Fields.id).is(id);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Stager.class);
    }

    public List<Stager> getAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Stager.class);
    }

    public Stager createStager(String eventId, User user) {
        return stagerRepo.save(Stager.builder()
                .eventId(eventId)
                .userId(user.id())
                .name(user.name())
                .profilePicture(null)
                .participationStatus(PENDING).build());
    }

    public void removeStager(String stagerId) {
        stagerRepo.deleteById(stagerId);
    }

    public Stager save(Stager rehearsal) {
        return stagerRepo.save(rehearsal);
    }

    public Stager getByEventAndUser(String eventId, String userId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId)
                .and(Stager.Fields.userId).is(userId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Stager.class);
    }

    public void deleteAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, Stager.class);
    }
}

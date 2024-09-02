package org.onstage.rehearsal.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.rehearsal.model.Rehearsal;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class RehearsalRepository {
    private final RehearsalRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<Rehearsal> findById(String id) {
        return repo.findById(id);
    }

    public List<Rehearsal> getAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Rehearsal.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Rehearsal.class);
    }

    public Rehearsal save(Rehearsal rehearsal) {
        return repo.save(rehearsal);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }

    public void deleteAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Rehearsal.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, Rehearsal.class);
    }

    public Rehearsal getById(String id) {
        Criteria criteria = Criteria.where(Rehearsal.Fields.id).is(id);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Rehearsal.class);
    }
}

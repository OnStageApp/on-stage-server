package org.onstage.rehearsal.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.rehearsal.model.RehearsalEntity;
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

    public Optional<RehearsalEntity> findById(String id) {
        return repo.findById(id);
    }

    public List<RehearsalEntity> getAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(RehearsalEntity.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, RehearsalEntity.class);
    }

    public RehearsalEntity save(RehearsalEntity rehearsal) {
        return repo.save(rehearsal);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }
}

package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.onstage.event.model.EventEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.onstage.event.model.EventEntity.Fields.date;
import static org.onstage.event.model.EventEntity.Fields.name;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<EventEntity> findById(String id) {
        return repo.findById(id);
    }

    public List<EventEntity> getAll(String search) {
        if (!isEmpty(search)) {
            Criteria criteria = Criteria.where(name).regex(search, "i");
            Query query = new Query(criteria);
            return mongoTemplate.find(query, EventEntity.class);
        }
        return repo.findAll();
    }

    public List<EventEntity> getAllByRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2022, Month.DECEMBER, 12, 11, 59);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(5);
        }
        Criteria criteria = Criteria.where(date).gte(startDate).lte(endDate);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, EventEntity.class);
    }

    public EventEntity save(EventEntity event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }


}

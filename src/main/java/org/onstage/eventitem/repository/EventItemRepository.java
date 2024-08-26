package org.onstage.eventitem.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.eventitem.model.EventItem;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventItemRepository {
    private final EventItemRepo eventItemRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<EventItem> getById(String id) {
        return eventItemRepo.findById(id);
    }

    public List<EventItem> getAll(String eventId) {
        Criteria criteria = Criteria.where("eventId").is(eventId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, EventItem.class, "event-items");
    }

    public EventItem save(EventItem eventItem) {
        return eventItemRepo.save(eventItem);
    }

    public void deleteAllByEventId(String eventId) {
        Criteria criteria = Criteria.where("eventId").is(eventId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, EventItem.class, "event-items");
    }
}
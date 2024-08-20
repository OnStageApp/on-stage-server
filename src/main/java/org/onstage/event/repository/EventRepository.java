package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.IEvent;
import org.onstage.event.model.EventEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.onstage.event.model.EventEntity.Fields.date;
import static org.onstage.event.model.EventEntity.Fields.name;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<EventEntity> findById(String id) {
        return repo.findById(id);
    }

    public List<IEvent> getAll(String search) {
        Criteria criteria = isEmpty(search) ? new Criteria() : Criteria.where(name).regex(search, "i");
        Aggregation aggregation = newAggregation(eventProjectionPipeline(criteria));
        return mongoTemplate.aggregate(aggregation, EventEntity.class, IEvent.class).getMappedResults();
    }

    public IEvent findEventProjectionById(String eventId) {
        Aggregation aggregation = newAggregation(eventProjectionPipeline(Criteria.where("_id").is(eventId)));
        return mongoTemplate.aggregate(aggregation, EventEntity.class, IEvent.class).getUniqueMappedResult();
    }

    public List<IEvent> getAllByRange(LocalDateTime startDate, LocalDateTime endDate) {
        startDate = (startDate != null) ? startDate : LocalDateTime.of(2022, Month.DECEMBER, 12, 11, 59);
        endDate = (endDate != null) ? endDate : LocalDateTime.now().plusYears(5);

        Criteria criteria = Criteria.where(date).gte(startDate).lte(endDate);
        Aggregation aggregation = newAggregation(eventProjectionPipeline(criteria));
        return mongoTemplate.aggregate(aggregation, EventEntity.class, IEvent.class).getMappedResults();
    }

    public EventEntity save(EventEntity event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }

    private List<AggregationOperation> eventProjectionPipeline(Criteria criteria) {
        return List.of(
                match(criteria),
                lookup("stagers", "stagerIds", "_id", "stagers"),
                project()
                        .and("_id").as("id")
                        .and("name").as("name")
                        .and("date").as("date")
                        .and("location").as("location")
                        .and("planners").as("planners")
                        .and("eventStatus").as("eventStatus")
                        .and("stagers").as("stagers")
        );
    }
}
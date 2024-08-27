package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.EventOverview;
import org.onstage.event.model.Event;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.isEmpty;
import static org.onstage.event.model.Event.Fields.dateTime;
import static org.onstage.event.model.Event.Fields.name;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<Event> findById(String id) {
        return repo.findById(id);
    }

    public EventOverview findEventProjectionById(String eventId) {
        Aggregation aggregation = newAggregation(eventProjectionPipeline(Criteria.where("_id").is(eventId)));
        return mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getUniqueMappedResult();
    }

    public List<EventOverview> getAllBySearch(String searchValue) {
        Criteria criteria = isEmpty(searchValue) ? new Criteria() : Criteria.where(name).regex(searchValue, "i");
        Aggregation aggregation = newAggregation(eventProjectionPipeline(criteria));
        return mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getMappedResults();
    }

    public List<EventOverview> getAll() {
        Aggregation aggregation = newAggregation(eventProjectionPipeline(new Criteria()));
        return mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getMappedResults();
    }

    public List<EventOverview> getAllUpcoming() {
        Criteria criteria = Criteria.where(dateTime).gte(LocalDateTime.now());
        Aggregation aggregation = newAggregation(eventProjectionPipeline(criteria));
        return mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getMappedResults();
    }

    public List<EventOverview> getAllPast() {
        Criteria criteria = Criteria.where(dateTime).lte(LocalDateTime.now());
        Aggregation aggregation = newAggregation(eventProjectionPipeline(criteria));
        return mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getMappedResults();
    }

    public Event save(Event event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }

    private List<AggregationOperation> eventProjectionPipeline(Criteria criteria) {
        return List.of(
                match(criteria),
                lookup("stagers", "_id", "eventId", "stagers"),
                project()
                        .and("_id").as("id")
                        .and("name").as("name")
                        .and("dateTime").as("dateTime")
                        .and("stagers.profilePicture").as("stagersPhotos"));
    }
}
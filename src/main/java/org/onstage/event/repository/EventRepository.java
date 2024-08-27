package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.EventOverview;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.model.Event;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<Event> findById(String id) {
        return repo.findById(id);
    }

//    public EventOverview findEventProjectionById(String eventId) {
//        Aggregation aggregation = newAggregation(eventProjectionPipeline(Criteria.where("_id").is(eventId)));
//        return mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getUniqueMappedResult();
//    }

    public PaginatedEventResponse getPaginatedEvents(Criteria criteria, int offset, int limit) {
        Aggregation aggregation = newAggregation(eventProjectionPipeline(criteria, offset, limit))
                .withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        List<EventOverview> events = mongoTemplate.aggregate(aggregation, Event.class, EventOverview.class).getMappedResults();

        Aggregation countAggregation = newAggregation(
                match(criteria),
                Aggregation.skip(offset + limit),
                Aggregation.limit(1)
        );

        List<EventOverview> nextEvents = mongoTemplate.aggregate(countAggregation, Event.class, EventOverview.class).getMappedResults();
        boolean hasMore = !nextEvents.isEmpty();

        return new PaginatedEventResponse(events, hasMore);
    }


    public Event save(Event event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }

    private List<AggregationOperation> eventProjectionPipeline(Criteria criteria, int offset, int limit) {
        List<AggregationOperation> operations = new ArrayList<>();
        if (criteria != null) {
            operations.add(match(criteria));
        }
        operations.add(lookup("stagers", "_id", "eventId", "stagers"));
        operations.add(project()
                .and("_id").as("id")
                .and("name").as("name")
                .and("dateTime").as("dateTime")
                .and("stagers.profilePicture").as("stagersPhotos"));

        if (offset > 0) {
            operations.add(Aggregation.skip(offset));
        }
        if (limit > 0) {
            operations.add(Aggregation.limit(limit));
        }

        return operations;
    }
}
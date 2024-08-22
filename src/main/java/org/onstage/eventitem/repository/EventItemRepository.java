package org.onstage.eventitem.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.eventitem.client.EventItem;
import org.onstage.eventitem.model.EventItemEntity;
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
public class EventItemRepository {
    private final EventItemRepo eventItemRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<EventItemEntity> getById(String id) {
        return eventItemRepo.findById(id);
    }

    public Optional<EventItem> findProjectionById(String id) {
        Aggregation aggregation = newAggregation(eventItemAggregationPipeline(Criteria.where("_id").is(id)));
        EventItem result = mongoTemplate.aggregate(aggregation, "event-items", EventItem.class)
                .getUniqueMappedResult();
        return Optional.ofNullable(result);
    }

    public List<EventItem> getAll(String eventId) {
        Aggregation aggregation = newAggregation(eventItemAggregationPipeline(Criteria.where("eventId").is(eventId)));
        return mongoTemplate.aggregate(aggregation, "event-items", EventItem.class)
                .getMappedResults();
    }

    public EventItemEntity save(EventItemEntity eventItem) {
        return eventItemRepo.save(eventItem);
    }

    private List<AggregationOperation> eventItemAggregationPipeline(Criteria criteria) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (criteria != null) {
            operations.add(match(criteria));
        }

        operations.add(lookup("songs", "songId", "_id", "songData"));
        operations.add(unwind("songData", true));
        operations.add(project()
                .and("_id").as("id")
                .and("name").as("name")
                .and("index").as("index")
                .and("eventType").as("eventType")
                .and("eventId").as("eventId")
                .and("songData").as("song")
        );
        operations.add(project()
                .and("id").as("id")
                .and("name").as("name")
                .and("index").as("index")
                .and("eventType").as("eventType")
                .and("eventId").as("eventId")
                .and("song._id").as("song.id")
                .and("song.title").as("song.title")
                .and("song.artistId").as("song.artistId")
                .and("song.key").as("song.key")
                .and("song.tempo").as("song.tempo")
        );

        return operations;
    }
}
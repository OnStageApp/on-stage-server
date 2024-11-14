package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.EventSearchType;
import org.onstage.enums.EventStatus;
import org.onstage.enums.MemberRole;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.model.Event;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<Event> findById(String id) {
        return repo.findById(id);
    }

    public Event getUpcomingPublishedEvent(String teamId, String userId) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.match(
                Criteria.where(Event.Fields.dateTime).gte(LocalDateTime.now())
                        .and(Event.Fields.eventStatus).is(EventStatus.PUBLISHED)
                        .and(Event.Fields.teamId).is(teamId)
        ));

        operations.add(Aggregation.lookup("stagers", "_id", "eventId", "stagers"));
        operations.add(Aggregation.match(
                Criteria.where("stagers.userId").is(userId)
                        .and("stagers.participationStatus").is(ParticipationStatus.CONFIRMED)
        ));
        operations.add(Aggregation.sort(Sort.Direction.ASC, Event.Fields.dateTime));
        operations.add(Aggregation.limit(1));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Event> results = mongoTemplate.aggregate(aggregation, "events", Event.class);

        return results.getUniqueMappedResult();
    }
    public PaginatedEventResponse getPaginatedEvents(TeamMember teamMember, String teamId, EventSearchType eventSearchType, String searchValue, int offset, int limit) {

        Aggregation aggregation = createEventAggregation(teamMember, teamId, eventSearchType, searchValue, offset, limit);
        AggregationResults<Event> aggregationResults = mongoTemplate.aggregate(aggregation, "events", Event.class);

        List<Event> events = aggregationResults.getMappedResults();
        long totalCount = mongoTemplate.count(createCountQuery(teamMember, teamId, eventSearchType, searchValue), Event.class);
        boolean hasMore = offset + limit < totalCount;

        return new PaginatedEventResponse(events, hasMore);
    }

    private Aggregation createEventAggregation(TeamMember teamMember, String teamId,
                                               EventSearchType eventSearchType, String searchValue, int offset, int limit) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.match(Criteria.where(Event.Fields.teamId).is(teamId)));

        if (teamMember.getRole() == MemberRole.NONE) {
            operations.add(Aggregation.match(Criteria.where(Event.Fields.eventStatus).is(EventStatus.PUBLISHED)));
        }

        if (searchValue != null && !searchValue.isEmpty()) {
            operations.add(Aggregation.match(Criteria.where(Event.Fields.name).regex(searchValue, "i")));
        } else if (EventSearchType.UPCOMING.equals(eventSearchType)) {
            operations.add(Aggregation.match(Criteria.where(Event.Fields.dateTime).gte(LocalDateTime.now())));
        } else if (EventSearchType.PAST.equals(eventSearchType)) {
            operations.add(Aggregation.match(Criteria.where(Event.Fields.dateTime).lte(LocalDateTime.now())));
        }

        operations.add(Aggregation.lookup("stagers", "_id", "eventId", "stagers"));
        operations.add(Aggregation.match(Criteria.where("stagers.teamMemberId").is(teamMember.getId())
                .and("stagers.participationStatus").is(ParticipationStatus.CONFIRMED)));

        operations.add(Aggregation.skip(offset));
        operations.add(Aggregation.limit(limit));

        return Aggregation.newAggregation(operations);
    }

    private Query createCountQuery(TeamMember teamMember, String teamId,
                                   EventSearchType eventSearchType, String searchValue) {
        Criteria criteria = Criteria.where(Event.Fields.teamId).is(teamId);

        if (teamMember.getRole() == MemberRole.NONE) {
            criteria.and(Event.Fields.eventStatus).is(EventStatus.PUBLISHED);
        }

        if (searchValue != null && !searchValue.isEmpty()) {
            criteria.and(Event.Fields.name).regex(searchValue, "i");
        } else if (EventSearchType.UPCOMING.equals(eventSearchType)) {
            criteria.and(Event.Fields.dateTime).gte(LocalDateTime.now());
        } else if (EventSearchType.PAST.equals(eventSearchType)) {
            criteria.and(Event.Fields.dateTime).lte(LocalDateTime.now());
        }

        return new Query(criteria);
    }


    public Event save(Event event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }

    public int countAllCreatedInInterval(String teamId) {
        Criteria criteria = Criteria.where(Event.Fields.teamId).is(teamId)
                .and("createdAt").gte(LocalDateTime.now().minusMonths(1));
        return (int) mongoTemplate.count(Query.query(criteria), Event.class);
    }
}
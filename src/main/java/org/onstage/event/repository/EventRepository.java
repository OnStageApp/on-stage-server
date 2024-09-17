package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.EventSearchType;
import org.onstage.enums.EventStatus;
import org.onstage.event.client.EventDTO;
import org.onstage.event.client.EventOverview;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.model.Event;
import org.onstage.stager.model.Stager;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<Event> findById(String id) {
        return repo.findById(id);
    }

    public EventDTO getUpcomingPublishedEvent() {
        Criteria criteria = Criteria.where("dateTime").gte(LocalDateTime.now())
                .and("eventStatus").is(EventStatus.PUBLISHED);

        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, "dateTime"));
        query.limit(1);

        return mongoTemplate.findOne(query, EventDTO.class, "events");
    }

    public PaginatedEventResponse getPaginatedEvents(EventSearchType eventSearchType, String searchValue, int offset, int limit, String teamMemberId, String teamId) {
        Query stagerQuery = new Query(Criteria.where(Stager.Fields.teamMemberId).is(teamMemberId));
        List<String> memberEventIds = mongoTemplate.find(stagerQuery, Stager.class)
                .stream()
                .map(Stager::eventId)
                .toList();
        Criteria eventCriteria = Criteria.where(Event.Fields.id).in(memberEventIds)
                .and("teamId").is(teamId);
        if (searchValue != null && !searchValue.isEmpty()) {
            eventCriteria = eventCriteria.and("name").regex(searchValue, "i");
        } else if (EventSearchType.UPCOMING.equals(eventSearchType)) {
            eventCriteria = eventCriteria.and("dateTime").gte(LocalDateTime.now());
        } else if (EventSearchType.PAST.equals(eventSearchType)) {
            eventCriteria = eventCriteria.and("dateTime").lte(LocalDateTime.now());
        }
        Query eventQuery = new Query(eventCriteria)
                .skip(offset)
                .limit(limit);
        List<Event> events = mongoTemplate.find(eventQuery, Event.class, "events");
        long totalCount = mongoTemplate.count(new Query(eventCriteria), "events");
        boolean hasMore = offset + limit < totalCount;

        List<EventOverview> eventOverviews = events.stream()
                .map(event -> EventOverview.builder()
                        .id(event.id())
                        .name(event.name())
                        .dateTime(event.dateTime())
                        .eventStatus(event.eventStatus())
                        .build())
                .toList();

        return new PaginatedEventResponse(eventOverviews, hasMore);
    }


    public Event save(Event event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }
}
package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.EventSearchType;
import org.onstage.enums.EventStatus;
import org.onstage.enums.MemberRole;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.model.Event;
import org.onstage.stager.model.Stager;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public Event getUpcomingPublishedEvent(String teamId) {
        Criteria criteria = Criteria.where(Event.Fields.dateTime).gte(LocalDateTime.now())
                .and(Event.Fields.eventStatus).is(EventStatus.PUBLISHED)
                .and(Event.Fields.teamId).is(teamId);

        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, Event.Fields.dateTime));
        query.limit(1);

        return mongoTemplate.findOne(query, Event.class);
    }

    public PaginatedEventResponse getPaginatedEvents(EventSearchType eventSearchType, String searchValue, int offset, int limit, TeamMember teamMember, String teamId) {
        Query stagerQuery = new Query(Criteria.where(Stager.Fields.teamMemberId).is(teamMember.id())
                .and(Stager.Fields.participationStatus).is(ParticipationStatus.CONFIRMED));

        List<String> memberEvents = mongoTemplate.find(stagerQuery, Stager.class)
                .stream()
                .map(Stager::eventId)
                .toList();

        Criteria eventCriteria = Criteria
                .where(Event.Fields.id).in(memberEvents)
                .and(Event.Fields.teamId).is(teamId);

        if (teamMember.role() == MemberRole.NONE) {
            eventCriteria.and(Event.Fields.eventStatus).is(EventStatus.PUBLISHED);
        }

        if (searchValue != null && !searchValue.isEmpty()) {
            eventCriteria = eventCriteria.and(Event.Fields.name).regex(searchValue, "i");
        } else if (EventSearchType.UPCOMING.equals(eventSearchType)) {
            eventCriteria = eventCriteria.and(Event.Fields.dateTime).gte(LocalDateTime.now());
        } else if (EventSearchType.PAST.equals(eventSearchType)) {
            eventCriteria = eventCriteria.and(Event.Fields.dateTime).lte(LocalDateTime.now());
        }
        Query eventQuery = new Query(eventCriteria)
                .skip(offset)
                .limit(limit);
        List<Event> events = mongoTemplate.find(eventQuery, Event.class);
        long totalCount = mongoTemplate.count(new Query(eventCriteria), Event.class);
        boolean hasMore = offset + limit < totalCount;

        return new PaginatedEventResponse(events, hasMore);
    }


    public Event save(Event event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }
}
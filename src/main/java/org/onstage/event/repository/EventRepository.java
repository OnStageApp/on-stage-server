package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.EventSearchType;
import org.onstage.enums.EventStatus;
import org.onstage.enums.MemberRole;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.client.EventDTO;
import org.onstage.event.client.EventOverview;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.model.Event;
import org.onstage.stager.model.Stager;
import org.onstage.teammember.model.TeamMember;
import org.onstage.user.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventRepository {
    private final EventRepo repo;
    private final MongoTemplate mongoTemplate;

    public Optional<Event> findById(String id) {
        return repo.findById(id);
    }

    public EventOverview getUpcomingPublishedEvent(String teamId) {
        Criteria criteria = Criteria.where("dateTime").gte(LocalDateTime.now())
                .and("eventStatus").is(EventStatus.PUBLISHED)
                .and("teamId").is(teamId);

        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, "dateTime"));
        query.limit(1);

        EventOverview eventOverview =  mongoTemplate.findOne(query, EventOverview.class, "events");
        return EventOverview.builder()
                .id(Objects.requireNonNull(eventOverview).id())
                .name(eventOverview.name())
                .dateTime(eventOverview.dateTime())
                .location(eventOverview.location())
                .eventStatus(eventOverview.eventStatus())
                .userIdsWithPhoto(getFirstThreeUserIdsWithPhoto(eventOverview.id()))
                .build();
    }

    public PaginatedEventResponse getPaginatedEvents(EventSearchType eventSearchType, String searchValue, int offset, int limit, TeamMember teamMember, String teamId) {
        Query stagerQuery = new Query(Criteria.where(Stager.Fields.teamMemberId).is(teamMember.id())
                .and(Stager.Fields.participationStatus).is(ParticipationStatus.CONFIRMED));

        List<String> memberEventIds = mongoTemplate.find(stagerQuery, Stager.class)
                .stream()
                .map(Stager::eventId)
                .toList();

        Criteria eventCriteria = Criteria
                .where(Event.Fields.id).in(memberEventIds)
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
        List<Event> events = mongoTemplate.find(eventQuery, Event.class, "events");
        long totalCount = mongoTemplate.count(new Query(eventCriteria), "events");
        boolean hasMore = offset + limit < totalCount;

        List<EventOverview> eventOverviews = events.stream()
                .map(event -> EventOverview.builder()
                        .id(event.id())
                        .name(event.name())
                        .dateTime(event.dateTime())
                        .eventStatus(event.eventStatus())
                        .userIdsWithPhoto(getFirstThreeUserIdsWithPhoto(event.id()))
                        .build())
                .toList();

        return new PaginatedEventResponse(eventOverviews, hasMore);
    }

    public List<String> getFirstThreeUserIdsWithPhoto(String eventId) {
        return mongoTemplate.find(
                        Query.query(Criteria.where(Stager.Fields.eventId).is(eventId)), Stager.class
                ).stream()
                .map(Stager::userId)
                .distinct()
                .flatMap(userId -> mongoTemplate.find(
                        Query.query(Criteria.where(User.Fields.id).is(userId).and(User.Fields.imageTimestamp).ne(null)),
                        User.class
                ).stream())
                .map(User::id)
                .limit(3)
                .toList();
    }


    public Event save(Event event) {
        return repo.save(event);
    }

    public String delete(String id) {
        repo.deleteById(id);
        return id;
    }
}
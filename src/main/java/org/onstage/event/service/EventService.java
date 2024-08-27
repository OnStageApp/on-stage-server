package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.EventSearchType;
import org.onstage.event.client.EventDTO;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.client.UpdateEventRequest;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.service.RehearsalService;
import org.onstage.stager.service.StagerService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final StagerService stagerService;
    private final RehearsalService rehearsalService;

    public Event getById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:%s was not found".formatted(id)));
    }

    public Event create(Event event, List<String> userIds, List<CreateRehearsalForEventRequest> rehearsals) {
        Event savedEvent = this.eventRepository.save(event);
        stagerService.createStagersForEvent(savedEvent.id(), userIds);
        rehearsalService.createRehearsalsForEvent(savedEvent.id(), rehearsals);
        log.info("Event {} has been saved", savedEvent.id());
        return savedEvent;
    }

    public String delete(String id) {
        return eventRepository.delete(id);
    }

    public Event update(String id, UpdateEventRequest request) {
        Event existingEvent = getById(id);
        Event updatedEvent = updateEventFromDTO(existingEvent, request);
        return eventRepository.save(updatedEvent);
    }

    private Event updateEventFromDTO(Event existingEvent, UpdateEventRequest request) {
        return Event.builder()
                .id(existingEvent.id())
                .name(request.name() == null ? existingEvent.name() : request.name())
                .dateTime(request.dateTime() == null ? existingEvent.dateTime() : request.dateTime())
                .location(request.location() == null ? existingEvent.location() : request.location())
                .eventStatus(request.eventStatus() == null ? existingEvent.eventStatus() : request.eventStatus())
                .build();
    }

    public PaginatedEventResponse getAllByFilter(EventSearchType eventSearchType, String searchValue, int offset, int limit) {
        Criteria criteria = new Criteria();

        if (searchValue != null) {
            criteria = Criteria.where("name").regex(searchValue, "i");
        } else if (EventSearchType.UPCOMING.equals(eventSearchType)) {
            criteria = Criteria.where("dateTime").gte(LocalDateTime.now());
        } else if (EventSearchType.PAST.equals(eventSearchType)) {
            criteria = Criteria.where("dateTime").lte(LocalDateTime.now());
        }
        return eventRepository.getPaginatedEvents(criteria, offset, limit);
    }

    public EventDTO getUpcomingPublishedEvent() {
        EventDTO eventDTO = eventRepository.getUpcomingPublishedEvent();
        return eventDTO;
    }
}

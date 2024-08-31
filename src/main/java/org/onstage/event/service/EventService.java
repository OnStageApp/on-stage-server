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
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.service.ReminderService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.onstage.enums.EventStatus.DRAFT;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final StagerService stagerService;
    private final RehearsalService rehearsalService;
    private final ReminderService reminderService;

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
        stagerService.deleteAllByEventId(id);
        rehearsalService.deleteAllByEventId(id);
        reminderService.deleteAllByEventId(id);
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
                .dateTime((existingEvent.eventStatus().equals(DRAFT) && request.dateTime() != null) ? request.dateTime() : existingEvent.dateTime())
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
        return eventRepository.getUpcomingPublishedEvent();
    }

    public Event duplicate(String id, LocalDateTime dateTime, String name) {
        Event event = getById(id);

        Event duplicatedEvent = Event.builder()
                .name(name)
                .location(event.location())
                .eventStatus(DRAFT)
                .dateTime(dateTime)
                .build();
        duplicatedEvent = eventRepository.save(duplicatedEvent);

        //duplicating stagers
        List<Stager> stagers = stagerService.getAllByEventId(id);
        stagerService.createStagersForEvent(duplicatedEvent.id(), stagers.stream().map(Stager::userId).toList());

        //duplicating reminders
        List<Reminder> reminders = reminderService.getAllByEventId(id);
        reminderService.createReminders(reminders.stream().map(Reminder::daysBefore).toList(), duplicatedEvent.id());

        return duplicatedEvent;
    }
}

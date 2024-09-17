package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.EventSearchType;
import org.onstage.event.client.EventDTO;
import org.onstage.event.client.EventOverview;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.client.UpdateEventRequest;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.service.RehearsalService;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.service.ReminderService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.onstage.enums.EventStatus.DRAFT;
import static org.onstage.exceptions.BadRequestException.eventNotFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final StagerService stagerService;
    private final RehearsalService rehearsalService;
    private final ReminderService reminderService;
    private final UserService userService;

    public Event getById(String id) {
        return eventRepository.findById(id).orElseThrow(BadRequestException::eventNotFound);
    }

    public Event save(Event event, List<String> teamMembersIds, List<CreateRehearsalForEventRequest> rehearsals, String teamId) {
        Event savedEvent = eventRepository.save(event.toBuilder().teamId(teamId).build());
        stagerService.createStagersForEvent(savedEvent.id(), teamMembersIds);
        rehearsalService.createRehearsalsForEvent(savedEvent.id(), rehearsals);
        log.info("Event {} has been saved", savedEvent.id());
        return savedEvent;
    }

    public String delete(String id) {
        Event event = getById(id);
        log.info("Deleting event {}", event.id());
        stagerService.deleteAllByEventId(event.id());
        rehearsalService.deleteAllByEventId(event.id());
        reminderService.deleteAllByEventId(event.id());
        return eventRepository.delete(event.id());
    }

    public Event update(Event existingEvent, UpdateEventRequest request) {
        log.info("Updating event {} with request {}", existingEvent.id(), request);
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

    public PaginatedEventResponse getAllByFilter(String teamMemberId, String teamId, EventSearchType eventSearchType, String searchValue, int offset, int limit) {
        PaginatedEventResponse paginatedEvents = eventRepository.getPaginatedEvents(eventSearchType, searchValue, offset, limit, teamMemberId, teamId);
        List<EventOverview> events = paginatedEvents.events().stream().map(event ->
                event.toBuilder().stagersPhotos(userService.getRandomUserIdsWithPhotos(event.id(), 4)).build()).toList();
        return paginatedEvents.toBuilder().events(events).build();
    }

    public EventDTO getUpcomingPublishedEvent() {
        return eventRepository.getUpcomingPublishedEvent();
    }

    public Event duplicate(Event event, LocalDateTime dateTime, String name) {
        Event duplicatedEvent = Event.builder()
                .name(name)
                .location(event.location())
                .eventStatus(DRAFT)
                .dateTime(dateTime)
                .build();
        duplicatedEvent = eventRepository.save(duplicatedEvent);

        List<Stager> stagers = stagerService.getAllByEventId(event.id());
        stagerService.createStagersForEvent(duplicatedEvent.id(), stagers.stream().map(Stager::teamMemberId).toList());

        List<Reminder> reminders = reminderService.getAllByEventId(event.id());
        reminderService.createReminders(reminders.stream().map(Reminder::daysBefore).toList(), duplicatedEvent.id());

        return duplicatedEvent;
    }
}

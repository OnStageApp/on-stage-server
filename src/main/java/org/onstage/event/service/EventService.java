package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.EventSearchType;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.client.UpdateEventRequest;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.service.RehearsalService;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.service.ReminderService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.service.TeamMemberService;
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
    private final EventItemRepository eventItemRepository;
    private final TeamMemberService teamMemberService;

    public Event getById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("Event"));
    }

    public Event save(Event event, List<String> teamMembersIds, List<CreateRehearsalForEventRequest> rehearsals, String teamId, String eventLeaderId) {
        event = event.toBuilder().teamId(teamId).build();
        Event savedEvent = eventRepository.save(event);
        stagerService.createStagersForEvent(savedEvent.getId(), teamMembersIds, eventLeaderId);
        rehearsalService.createRehearsalsForEvent(savedEvent.getId(), rehearsals);
        log.info("Event {} has been saved", savedEvent.getId());
        return savedEvent;
    }

    public String delete(String id) {
        Event event = getById(id);
        log.info("Deleting event {}", event.getId());
        stagerService.deleteAllByEventId(event.getId());
        rehearsalService.deleteAllByEventId(event.getId());
        reminderService.deleteAllByEventId(event.getId());
        return eventRepository.delete(event.getId());
    }

    public Event update(Event existingEvent, UpdateEventRequest request) {
        log.info("Updating event {} with request {}", existingEvent.getId(), request);
        Event updatedEvent = updateEventFromDTO(existingEvent, request);
        return eventRepository.save(updatedEvent);
    }

    private Event updateEventFromDTO(Event existingEvent, UpdateEventRequest request) {
        return Event.builder()
                .id(existingEvent.getId())
                .name(request.name() == null ? existingEvent.getName() : request.name())
                .dateTime((existingEvent.getEventStatus().equals(DRAFT) && request.dateTime() != null) ? request.dateTime() : existingEvent.getDateTime())
                .location(request.location() == null ? existingEvent.getLocation() : request.location())
                .eventStatus(request.eventStatus() == null ? existingEvent.getEventStatus() : request.eventStatus())
                .teamId(existingEvent.getTeamId())
                .build();
    }

    public PaginatedEventResponse getAllByFilter(String teamMemberId, String teamId, EventSearchType eventSearchType, String searchValue, int offset, int limit) {
        TeamMember teamMember = teamMemberService.getById(teamMemberId);
        return eventRepository.getPaginatedEvents(teamMember, teamId, eventSearchType, searchValue, offset, limit);
    }

    public Event getUpcomingPublishedEvent(String teamId) {
        return eventRepository.getUpcomingPublishedEvent(teamId);
    }

    public Event duplicate(Event event, LocalDateTime dateTime, String name, String eventLeaderId) {
        Event duplicatedEvent = Event.builder()
                .name(name)
                .location(event.getLocation())
                .eventStatus(DRAFT)
                .dateTime(dateTime)
                .teamId(event.getTeamId())
                .build();
        duplicatedEvent = eventRepository.save(duplicatedEvent);

        List<Stager> stagers = stagerService.getAllByEventId(event.getId());
        stagerService.createStagersForEvent(duplicatedEvent.getId(), stagers.stream().map(Stager::teamMemberId).toList(), eventLeaderId);

        List<Reminder> reminders = reminderService.getAllByEventId(event.getId());
        reminderService.createReminders(reminders.stream().map(Reminder::daysBefore).toList(), duplicatedEvent.getId());

        List<EventItem> eventItems = eventItemRepository.getAll(event.getId());
        for (EventItem eventItem : eventItems) {
            eventItemRepository.save(EventItem.builder()
                    .eventId(duplicatedEvent.getId())
                    .eventType(eventItem.eventType())
                    .songId(eventItem.songId())
                    .index(eventItem.index())
                    .name(eventItem.name())
                    .build());
        }

        return duplicatedEvent;
    }

    public int countAllCreatedInInterval(String teamId) {
        return eventRepository.countAllCreatedInInterval(teamId);
    }
}

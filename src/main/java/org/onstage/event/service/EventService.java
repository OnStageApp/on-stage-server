package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.EventSearchType;
import org.onstage.enums.NotificationType;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.client.PaginatedEventResponse;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.service.RehearsalService;
import org.onstage.reminder.model.Reminder;
import org.onstage.reminder.service.ReminderService;
import org.onstage.stager.service.StagerService;
import org.onstage.team.model.Team;
import org.onstage.team.service.TeamService;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.service.TeamMemberService;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.onstage.enums.EventStatus.*;

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
    private final NotificationService notificationService;
    private final TeamService teamService;
    private final UserService userService;

    public Event getById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
    }

    public Event save(Event event, List<String> teamMembersIds, List<CreateRehearsalForEventRequest> rehearsals, String teamId, String requestedByUser) {
        event = event.toBuilder().teamId(teamId).createdByUser(requestedByUser).build();
        Event savedEvent = eventRepository.save(event);

        stagerService.createStagersForEvent(savedEvent, teamMembersIds);
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
        eventItemRepository.deleteAllByEventId(event.getId());
        return eventRepository.delete(event.getId());
    }

    public Event update(String id, Event request, String requestedByUser) {
        log.info("{} updated event {} with request {}", requestedByUser, id, request);
        Event existingEvent = getById(id);
        existingEvent.setName(request.getName() != null ? request.getName() : existingEvent.getName());
        existingEvent.setDateTime(request.getDateTime() != null ? request.getDateTime() : existingEvent.getDateTime());
        existingEvent.setLocation(request.getLocation() != null ? request.getLocation() : existingEvent.getLocation());
        existingEvent.setEventStatus(request.getEventStatus() != null ? request.getEventStatus() : existingEvent.getEventStatus());
        existingEvent = eventRepository.save(existingEvent);

        notifyStagersAboutEvent(existingEvent, requestedByUser);

        return existingEvent;
    }

    public PaginatedEventResponse getAllByFilter(String teamMemberId, String teamId, EventSearchType eventSearchType, String searchValue, int offset, int limit) {
        TeamMember teamMember = teamMemberService.getById(teamMemberId);
        return eventRepository.getPaginatedEvents(teamMember, teamId, eventSearchType, searchValue, offset, limit);
    }

    public Event getUpcomingPublishedEvent(String teamId, String userId) {
        return eventRepository.getUpcomingPublishedEvent(teamId, userId);
    }

    public Event duplicate(Event event, LocalDateTime dateTime, String name, String requestedByUser) {
        log.info("Duplicating event {} with name {} and date {}", event.getId(), name, dateTime);
        Event duplicatedEvent = Event.builder()
                .name(name)
                .location(event.getLocation())
                .eventStatus(DRAFT)
                .dateTime(dateTime)
                .teamId(event.getTeamId())
                .createdByUser(requestedByUser)
                .build();
        duplicatedEvent = eventRepository.save(duplicatedEvent);

        TeamMember teamMember = teamMemberService.getByUserAndTeam(requestedByUser, event.getTeamId());
        stagerService.create(duplicatedEvent, teamMember.getId());

        List<Reminder> reminders = reminderService.getAllByEventId(event.getId());
        reminderService.createReminders(reminders.stream().map(Reminder::getDaysBefore).toList(), duplicatedEvent.getId());

        List<EventItem> eventItems = eventItemRepository.getAll(event.getId());
        for (EventItem eventItem : eventItems) {
            log.info("Duplicating event item {} for event {}", eventItem.getId(), duplicatedEvent.getId());
            eventItemRepository.save(EventItem.builder()
                    .eventId(duplicatedEvent.getId())
                    .eventType(eventItem.getEventType())
                    .songId(eventItem.getSongId())
                    .index(eventItem.getIndex())
                    .name(eventItem.getName())
                    .build());
        }

        return duplicatedEvent;
    }

    private void notifyStagersAboutEvent(Event event, String requestedByUser) {
        if (event.getEventStatus() == DELETED) {
            String description = String.format("%s cancelled event %s", requestedByUser, event.getName());
            String title = "Event cancelled";

            stagerService.getStagersToNotify(event.getId(), requestedByUser, ParticipationStatus.CONFIRMED).forEach(stager -> {
                notificationService.sendNotificationToUser(NotificationType.EVENT_DELETED, stager.getUserId(), description, title, event.getTeamId(), NotificationParams.builder().userId(requestedByUser).build());
            });
        }

        if (event.getEventStatus() == PUBLISHED) {
            Team team = teamService.getById(event.getTeamId());
            String description = String.format("You have been invited to %s event. Team %s", event.getName(), team.getName());
            String title = event.getName();
            Integer stagerCount = stagerService.countByEventId(event.getId());

            List<String> usersWithPhoto = userService.getUserIdsWithPhotoFromEvent(event.getId());
            stagerService.getStagersToNotify(event.getId(), requestedByUser, ParticipationStatus.PENDING).forEach(stager -> {
                notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_REQUEST, stager.getUserId(), description, title, team.getId(),
                        NotificationParams.builder().stagerId(stager.getId()).eventId(event.getId()).date(event.getDateTime()).usersWithPhoto(usersWithPhoto).participantsCount(stagerCount).build());
            });
        }
    }
}

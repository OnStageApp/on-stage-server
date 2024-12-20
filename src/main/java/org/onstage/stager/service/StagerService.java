package org.onstage.stager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.NotificationType;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.eventitem.service.EventItemService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.onstage.enums.EventStatus.PUBLISHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagerService {
    private final StagerRepository stagerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EventItemService eventItemService;
    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final TeamRepository teamRepository;

    public Stager getById(String id) {
        return stagerRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("stager"));
    }

    public List<Stager> getAllByEventId(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public List<Stager> getStagersToNotify(String eventId, String requestedByUser, ParticipationStatus participationStatus) {
        return stagerRepository.getStagersToNotify(eventId, requestedByUser, participationStatus);
    }

    public List<Stager> createStagersForEvent(Event event, List<String> teamMembersIds) {
        return teamMembersIds.stream().map(teamMemberId -> create(event, teamMemberId)).collect(toList());
    }

    public Stager create(Event event, String teamMemberId) {
        log.info("Creating stager for event {} and team member {}", event.getId(), teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(() -> BadRequestException.resourceNotFound("teamMember"));
        Stager stager = stagerRepository.createStager(event, teamMember, event.getCreatedByUser());

        notifyStager(event, stager);
        return stager;
    }

    public String remove(String stagerId) {
        log.info("Removing stager with id {}", stagerId);
        Stager stager = getById(stagerId);
        Event event = eventRepository.findById(stager.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        stagerRepository.deleteById(stagerId);

        notifyRemovedStager(event, stager);
        return stagerId;
    }

    public Stager update(String id, Stager request) {
        log.info("Updating stager with id {} with request {}", id, request);
        Stager existingStager = getById(id);
        existingStager.setParticipationStatus(request.getParticipationStatus() == null ? existingStager.getParticipationStatus() : request.getParticipationStatus());
        stagerRepository.save(existingStager);

        if (request.getParticipationStatus() == ParticipationStatus.DECLINED) {
            eventItemService.removeLeadVocalsByStagerId(id);
        }
        notifyEventEditor(existingStager);
        return existingStager;
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all stagers for event {}", eventId);
        List<Stager> stagersToDelete = getAllByEventId(eventId);
        stagersToDelete.forEach(stager -> delete(stager.getId()));
    }

    public void delete(String id) {
        log.info("Deleting stager with id {}", id);
        eventItemService.removeLeadVocalsByStagerId(id);
        stagerRepository.deleteById(id);
    }

    public Integer countByEventId(String eventId) {
        return stagerRepository.countByEventId(eventId);
    }

    public void removeAllByTeamMemberId(String teamMemberId) {
        log.info("Removing all stagers for team member {}", teamMemberId);
        List<Stager> stagers = stagerRepository.getAllByTeamMemberId(teamMemberId);
        stagers.forEach(stager -> delete(stager.getId()));
    }

    public void notifyStager(Event event, Stager stager) {
        if (event.getEventStatus() == PUBLISHED && stager.getParticipationStatus() != ParticipationStatus.CONFIRMED) {
            log.info("Notifying stager {} about event {}", stager.getId(), event.getId());
            Team team = teamRepository.findById(event.getTeamId()).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
            String description = String.format("You have been invited to %s event. Team %s", event.getName(), team.getName());
            String title = event.getName();
            Integer stagerCount = countByEventId(event.getId());

            List<String> usersWithPhoto = userService.getUserIdsWithPhotoFromEvent(event.getId());
            NotificationParams params = NotificationParams.builder().stagerId(stager.getId()).eventId(event.getId()).date(event.getDateTime()).usersWithPhoto(usersWithPhoto).participantsCount(stagerCount).build();
            notificationService.deleteNotificationByEventId(NotificationType.EVENT_INVITATION_REQUEST, event.getId(), stager.getUserId());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_REQUEST, stager.getUserId(), description, title, team.getId(), params);

        }
    }

    private void notifyEventEditor(Stager stager) {
        Event event = eventRepository.findById(stager.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        User user = userService.getById(stager.getUserId());
        if (stager.getParticipationStatus() == ParticipationStatus.DECLINED) {
            log.info("Notifying event editor about stager {} declining the invitation to event {}", stager.getId(), event.getId());
            String description = String.format("%s declined your invitation to the event %s", user.getName(), event.getName());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_DECLINED, event.getCreatedByUser(), description, null, event.getTeamId(),
                    NotificationParams.builder().eventId(event.getId()).userId(stager.getUserId()).build());
        }

        if (stager.getParticipationStatus() == ParticipationStatus.CONFIRMED) {
            log.info("Notifying event editor about stager {} accepting the invitation to event {}", stager.getId(), event.getId());
            String description = String.format("%s accepted your invitation to the event %s", user.getName(), event.getName());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_ACCEPTED, event.getCreatedByUser(), description, null, event.getTeamId(),
                    NotificationParams.builder().eventId(event.getId()).userId(stager.getUserId()).build());
        }
    }

    private void notifyRemovedStager(Event event, Stager stager) {
        String description = String.format("You have been removed from %s", event.getName());
        notificationService.sendNotificationToUser(NotificationType.STAGER_REMOVED, stager.getUserId(), description, null, event.getTeamId(),
                NotificationParams.builder().teamId(event.getTeamId()).build());
    }
}

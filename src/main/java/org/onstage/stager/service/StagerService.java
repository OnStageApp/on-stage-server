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

    public Stager getByEventAndTeamMember(String eventId, String teamMemberId) {
        return stagerRepository.getByEventAndTeamMember(eventId, teamMemberId);
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
        Stager stager = stagerRepository.createStager(event.getId(), teamMember, event.getCreatedByUser());

        notifyStager(event, stager);
        return stager;
    }

    public String remove(String stagerId) {
        log.info("Removing stager with id {}", stagerId);
        stagerRepository.removeStager(stagerId);
        return stagerId;
    }

    public Stager update(String id, Stager request) {
        Stager existingStager = getById(id);
        existingStager.setParticipationStatus(request.getParticipationStatus() == null ? existingStager.getParticipationStatus() : request.getParticipationStatus());
        stagerRepository.save(existingStager);

        notifyEventEditor(existingStager);
        return existingStager;
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all stagers for event {}", eventId);
        stagerRepository.deleteAllByEventId(eventId);
    }

    public Integer countByEventId(String eventId) {
        return stagerRepository.countByEventId(eventId);
    }

    public void removeAllByTeamMemberId(String teamMemberId) {
        log.info("Removing all stagers for team member {}", teamMemberId);
        List<Stager> stagers = stagerRepository.getAllByTeamMemberId(teamMemberId);
        stagers.forEach(stager -> {
            eventItemService.removeLeadVocalFromEvent(stager.getId(), stager.getEventId());
            stagerRepository.removeStager(stager.getId());
        });
    }

    public void notifyStager(Event event, Stager stager) {
        if (event.getEventStatus() == PUBLISHED && stager.getParticipationStatus() != ParticipationStatus.CONFIRMED) {
            Team team = teamRepository.findById(event.getTeamId()).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
            String description = String.format("You have been invited to %s event. Team %s", event.getName(), team.getName());
            String title = event.getName();
            Integer stagerCount = countByEventId(event.getId());

            List<String> usersWithPhoto = userService.getUserIdsWithPhotoFromEvent(event.getId());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_REQUEST, stager.getUserId(), description, title, team.getId(),
                    NotificationParams.builder().stagerId(stager.getId()).eventId(event.getId()).date(event.getDateTime()).usersWithPhoto(usersWithPhoto).participantsCount(stagerCount).build());

        }
    }

    private void notifyEventEditor(Stager stager) {
        if (stager.getParticipationStatus() == ParticipationStatus.DECLINED) {
            eventItemService.removeLeadVocalFromEvent(stager.getId(), stager.getEventId());

            Event event = eventRepository.findById(stager.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
            String description = String.format("%s declined your invitation to the event %s", stager.getName(), event.getName());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_DECLINED, event.getCreatedByUser(), description, null, event.getTeamId(),
                    NotificationParams.builder().eventId(event.getId()).userId(stager.getUserId()).build());
        }

        if (stager.getParticipationStatus() == ParticipationStatus.CONFIRMED) {
            Event event = eventRepository.findById(stager.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
            String description = String.format("%s accepted your invitation to the event %s", stager.getName(), event.getName());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_ACCEPTED, event.getCreatedByUser(), description, null, event.getTeamId(),
                    NotificationParams.builder().eventId(event.getId()).userId(stager.getUserId()).build());
        }
    }
}

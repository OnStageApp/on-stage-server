package org.onstage.stager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.event.service.EventService;
import org.onstage.eventitem.service.EventItemService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.client.NotificationType;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagerService {
    private final StagerRepository stagerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EventItemService eventItemService;
    private final NotificationService notificationService;
    private final EventRepository eventRepository;

    public Stager getById(String id) {
        return stagerRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("stager"));
    }

    public Stager getByEventAndTeamMember(String eventId, String teamMemberId) {
        return stagerRepository.getByEventAndTeamMember(eventId, teamMemberId);
    }

    public List<Stager> getAllByEventId(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public List<Stager> createStagersForEvent(Event event, List<String> teamMembersIds) {
        return teamMembersIds.stream().map(teamMemberId -> create(event, teamMemberId)).collect(toList());
    }

    public Stager create(Event event, String teamMemberId) {
        log.info("Creating stager for event {} and team member {}", event.getId(), teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(() -> BadRequestException.resourceNotFound("teamMember"));
        checkStagerAlreadyExists(event.getId(), teamMemberId);
        return stagerRepository.createStager(event.getId(), teamMember);
    }

    public String remove(String stagerId) {
        log.info("Removing stager with id {}", stagerId);
        stagerRepository.removeStager(stagerId);
        return stagerId;
    }

    private void checkStagerAlreadyExists(String eventId, String teamMemberId) {
        Stager stager = getByEventAndTeamMember(eventId, teamMemberId);
        if (stager != null) {
            throw BadRequestException.stagerAlreadyCreated();
        }
    }

    public Stager update(String id, Stager request) {
        Stager existingStager = getById(id);

        existingStager = existingStager.toBuilder()
                .participationStatus(request.participationStatus() != null ? request.participationStatus() : existingStager.participationStatus())
                .build();

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

    private void notifyEventEditor(Stager stager) {
        if (stager.participationStatus() == ParticipationStatus.DECLINED) {
            eventItemService.removeLeadVocalFromEvent(stager.id(), stager.eventId());

            Event event = eventRepository.findById(stager.eventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
            String description = String.format("%s declined your invitation to the event %s", stager.name(), event.getName());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_DECLINED, event.getCreatedBy(), description, null, NotificationParams.builder().eventId(event.getId()).userId(stager.userId()).build());
        }

        if (stager.participationStatus() == ParticipationStatus.CONFIRMED) {
            Event event = eventRepository.findById(stager.eventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
            String description = String.format("%s accepted your invitation to the event %s", stager.name(), event.getName());
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_ACCEPTED, event.getCreatedBy(), description, null, NotificationParams.builder().eventId(event.getId()).userId(stager.userId()).build());
        }
    }
}

package org.onstage.stager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.MemberRole;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.model.Event;
import org.onstage.eventitem.service.EventItemService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.client.NotificationStatus;
import org.onstage.notification.client.NotificationType;
import org.onstage.notification.service.NotificationService;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.team.model.Team;
import org.onstage.team.service.TeamService;
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
    private final TeamService teamService;

    public Stager getById(String id) {
        return stagerRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("Stager"));
    }

    public Stager getByEventAndTeamMember(String eventId, String teamMemberId) {
        return stagerRepository.getByEventAndTeamMember(eventId, teamMemberId);
    }

    public List<Stager> getAllByEventId(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public List<Stager> createStagersForEvent(Event event, List<String> teamMembersIds, String eventLeaderId) {
        if (eventLeaderId != null) {
            teamMembersIds = teamMembersIds.stream().filter(id -> !id.equals(eventLeaderId)).toList();
        }
        return teamMembersIds.stream().map(teamMemberId -> create(event, teamMemberId)).collect(toList());
    }

    public Stager create(Event event, String teamMemberId) {
        log.info("Creating stager for event {} and team member {}", event.getId(), teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(() -> BadRequestException.resourceNotFound("Team member"));
        checkStagerAlreadyExists(event.getId(), teamMemberId);

        Team team = teamService.getById(teamMember.teamId());
        Stager stager = stagerRepository.createStager(event.getId(), teamMember);
        if (teamMember.role() != MemberRole.LEADER) {
            String description = String.format("You have been invited to %s event. Team %s", event.getName(), team.name());
            String title = event.getName();
            notificationService.sendNotificationToUser(NotificationType.EVENT_INVITATION_REQUEST, stager.userId(), description, title, event.getId());
        }
        return stager;

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

    public Stager update(Stager existingStager, StagerDTO request) {
        if (request.participationStatus() == ParticipationStatus.DECLINED) {
            eventItemService.removeLeadVocalFromEvent(existingStager.id(), existingStager.eventId());
        }
        Stager updatedStager = existingStager
                .toBuilder()
                .participationStatus(request.participationStatus() != null ? request.participationStatus() : existingStager.participationStatus())
                .build();

        return stagerRepository.save(updatedStager);
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all stagers for event {}", eventId);
        stagerRepository.deleteAllByEventId(eventId);
    }

    public Integer countByEventId(String eventId) {
        return stagerRepository.countByEventId(eventId);
    }

}

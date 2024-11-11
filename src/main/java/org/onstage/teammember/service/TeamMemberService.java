package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.onstage.enums.MemberRole;
import org.onstage.enums.NotificationType;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.sendgrid.SendGridService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.onstage.team.model.Team;
import org.onstage.team.service.TeamService;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.onstage.enums.MemberInviteStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;
    private final StagerService stagerService;
    private final TeamService teamService;
    private final SendGridService sendGridService;
    private final NotificationService notificationService;

    public TeamMember getById(String id) {
        return teamMemberRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("teamMember"));
    }

    public TeamMember getByUserAndTeam(String userId, String teamId) {
        return teamMemberRepository.getByUserAndTeam(userId, teamId);
    }

    public TeamMember save(TeamMember teamMember) {
        TeamMember existingTeamMember = getByUserAndTeam(teamMember.userId(), teamMember.teamId());
        if (existingTeamMember != null) {
            log.info("Team member {} already exists", existingTeamMember.id());
            return existingTeamMember;
        }
        User user = userService.getById(teamMember.userId());
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember.toBuilder().name(user.getName()).build());
        log.info("Team member {} has been saved", savedTeamMember.id());
        return savedTeamMember;
    }

    public String delete(String id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("teamMember"));
        log.info("Deleting team member {}", id);
        teamMemberRepository.delete(id);
        return teamMember.id();
    }

    public List<TeamMember> getAllByTeam(String teamId, String userId, boolean includeCurrentUser) {
        return teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);
    }

    public TeamMember update(String id, TeamMember teamMember) {
        TeamMember existingTeamMember = getById(id);
        log.info("Updating team member {} with request {}", existingTeamMember.id(), teamMember);

        existingTeamMember = teamMemberRepository.save(existingTeamMember.toBuilder()
                .role(teamMember.role() != null ? teamMember.role() : existingTeamMember.role())
                .inviteStatus(teamMember.inviteStatus() != null ? teamMember.inviteStatus() : existingTeamMember.inviteStatus())
                .position(teamMember.position() != null ? teamMember.position() : existingTeamMember.position())
                .build());

        notifyLeader(existingTeamMember);
        return existingTeamMember;
    }

    public List<TeamMember> getAllUninvitedMembers(String eventId, String userId, String teamId, boolean includeCurrentUser) {
        final List<Stager> stagers = stagerService.getAllByEventId(eventId);
        final List<TeamMember> teamMembers = teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);

        return teamMembers.stream()
                .filter(teamMember -> teamMember.inviteStatus() == CONFIRMED)
                .filter(member -> !stagers.stream().map(Stager::teamMemberId).toList().contains(member.id()))
                .collect(Collectors.toList());
    }

    public TeamMember inviteMember(String email, MemberRole memberRole, String teamMemberInvited, String teamId, String invitedBy
    ) {
        User invitedUser;
        TeamMember teamMember;
        Team team = teamService.getById(teamId);

        if (Strings.isNotEmpty(email)) {
            invitedUser = userService.getByEmail(email);

            if (invitedUser == null) {
                sendGridService.sendInviteToTeamEmail(email, team.name());
                return null;
            }

            teamMember = teamMemberRepository.save(
                    TeamMember.builder()
                            .teamId(teamId)
                            .userId(invitedUser.getId())
                            .role(memberRole)
                            .name(invitedUser.getName())
                            .inviteStatus(PENDING)
                            .build()
            );
        } else if (Strings.isNotEmpty(teamMemberInvited)) {
            teamMember = getById(teamMemberInvited);
            invitedUser = userService.getById(teamMember.userId());
        } else {
            throw BadRequestException.invalidRequest();
        }

        if (invitedUser == null) {
            throw BadRequestException.resourceNotFound("user");
        }

        notifyInvitedUser(teamId, invitedBy, invitedUser, teamMember.id());
        return teamMember;
    }

    public Integer countByTeamId(String teamId) {
        return teamMemberRepository.countByTeamId(teamId);
    }

    public List<String> getMemberWithPhotoIds(String teamId) {
        return teamMemberRepository.getMemberWithPhotoIds(teamId);
    }

    public MemberRole getRole(Team team, String userId) {
        return teamMemberRepository.getByUserAndTeam(userId, team.id()).role();
    }

    private void notifyLeader(TeamMember teamMember) {
        if (teamMember.inviteStatus() == CONFIRMED) {
            Team team = teamService.getById(teamMember.teamId());
            String description = String.format("%s accepted your invitation to join %s", teamMember.name(), team.name());
            notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_ACCEPTED, team.leaderId(), description, null, NotificationParams.builder().teamMemberId(teamMember.id()).userId(teamMember.userId()).build());
        }

        if (teamMember.inviteStatus() == DECLINED) {
            Team team = teamService.getById(teamMember.teamId());
            String description = String.format("%s declined your invitation to join %s", teamMember.name(), team.name());
            notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_DECLINED, team.leaderId(), description, null, NotificationParams.builder().teamMemberId(teamMember.id()).userId(teamMember.userId()).build());
        }
    }

    private void notifyInvitedUser(String teamName, String invitedBy, User invitedUser, String teamMemberId) {
        User invitedByUser = userService.getById(invitedBy);
        String description = String.format("%s invited you to join %s team", invitedByUser.getName(), teamName);
        notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_REQUEST, invitedUser.getId(), description, teamName, NotificationParams.builder().teamMemberId(teamMemberId).build());
    }
}

package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.enums.MemberRole;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.action.CreateNotificationAction;
import org.onstage.notification.client.Notification;
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

import static org.onstage.enums.MemberInviteStatus.CONFIRMED;
import static org.onstage.enums.MemberInviteStatus.PENDING;
import static org.onstage.notification.client.NotificationStatus.NEW;
import static org.onstage.notification.client.NotificationType.TEAM_INVITATION_REQUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;
    private final StagerService stagerService;
    private final TeamService teamService;
    private final SendGridService sendGridService;
    private final CreateNotificationAction createNotificationAction;
    private final UserSecurityContext userSecurityContext;

    public TeamMember getById(String id) {
        return teamMemberRepository.findById(id).orElseThrow(BadRequestException::teamMemberNotFound);
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
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember.toBuilder().name(user.name()).build());
        log.info("Team member {} has been saved", savedTeamMember.id());
        return savedTeamMember;
    }

    public String delete(String id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(BadRequestException::teamMemberNotFound);
        log.info("Deleting team member {}", id);
        teamMemberRepository.delete(id);
        return teamMember.id();
    }

    public List<TeamMember> getAllByTeam(String teamId, String userId, boolean includeCurrentUser) {
        return teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);
    }

    public TeamMember update(TeamMember existingTeamMember, TeamMember teamMember) {
        log.info("Updating team member {} with request {}", existingTeamMember.id(), teamMember);

        return teamMemberRepository.save(existingTeamMember.toBuilder()
                .role(teamMember.role() != null ? teamMember.role() : existingTeamMember.role())
                .inviteStatus(teamMember.inviteStatus() != null ? teamMember.inviteStatus() : existingTeamMember.inviteStatus())
                .position(teamMember.position() != null ? teamMember.position() : existingTeamMember.position())
                .build());
    }

    public List<TeamMember> getAllUninvitedMembers(String eventId, String userId, String teamId, boolean includeCurrentUser) {
        final List<Stager> stagers = stagerService.getAllByEventId(eventId);
        final List<TeamMember> teamMembers = teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);

        return teamMembers.stream()
                .filter(teamMember -> teamMember.inviteStatus() == CONFIRMED)
                .filter(member -> !stagers.stream().map(Stager::teamMemberId).toList().contains(member.id()))
                .collect(Collectors.toList());
    }

    public TeamMember inviteMember(String email, MemberRole memberRole, String teamId) {
        User user = userService.getByEmail(email);
        if (user == null) {
            throw BadRequestException.userNotFound();
        }
        TeamMember existingTeamMember = getByUserAndTeam(user.id(), teamId);
        if (existingTeamMember != null) {
            throw BadRequestException.userAlreadyInTeam();
        }

        Team team = teamService.getById(teamId);
        sendGridService.sendInviteToTeamEmail(user, team.name());

        User currentUser = userService.getById(userSecurityContext.getUserId());
        createNotificationAction.execute(Notification.builder()
                .type(TEAM_INVITATION_REQUEST)
                .status(NEW)
                .description("%s from the %s team is inviting you".formatted(currentUser.name(), team.name()))
                .userId(user.id())
                .build());

        return save(TeamMember.builder()
                .teamId(teamId)
                .userId(user.id())
                .role(memberRole)
                .name(user.name())
                .inviteStatus(PENDING)
                .build());
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
}

package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.enums.MemberPosition;
import org.onstage.enums.MemberRole;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.client.NotificationStatus;
import org.onstage.notification.client.NotificationType;
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

import static org.onstage.enums.MemberInviteStatus.CONFIRMED;
import static org.onstage.enums.MemberInviteStatus.PENDING;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;
    private final StagerService stagerService;
    private final TeamService teamService;
    private final SendGridService sendGridService;
    private final UserSecurityContext userSecurityContext;
    private final NotificationService notificationService;

    public TeamMember getById(String id) {
        return teamMemberRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("Team member"));
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
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("Team member"));
        log.info("Deleting team member {}", id);
        teamMemberRepository.delete(id);
        return teamMember.id();
    }

    public List<TeamMember> getAllByTeam(String teamId, String userId, boolean includeCurrentUser) {
        return teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);
    }

    public List<TeamMember> getAllByTeam(String teamId) {
        return teamMemberRepository.getAllByTeam(teamId);
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
        //TODO if user does not exist send the email
        //sendGridService.sendInviteToTeamEmail(user, team.name());
        if (user == null) {
            throw BadRequestException.resourceNotFound("User");
        }
        TeamMember existingTeamMember = getByUserAndTeam(user.getId(), teamId);
        if (existingTeamMember != null) {
            throw BadRequestException.userAlreadyInTeam();
        }

        Team team = teamService.getById(teamId);
        TeamMember teamMember = save(TeamMember.builder()
                .teamId(teamId)
                .userId(user.getId())
                .role(memberRole)
                .name(user.getName())
                .inviteStatus(PENDING)
                .build());

        User leader = userService.getById(team.leaderId());
        String description = String.format("%s invited you to join %s team", leader.getName(), team.name());
        String title = team.name();
        notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_REQUEST, user.getId(), description, title, null);
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
}

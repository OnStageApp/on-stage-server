package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.onstage.enums.MemberRole;
import org.onstage.enums.NotificationType;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.plan.model.Plan;
import org.onstage.plan.service.PlanService;
import org.onstage.sendgrid.SendGridService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
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
    private final TeamRepository teamRepository;
    private final SendGridService sendGridService;
    private final NotificationService notificationService;
    private final PlanService planService;

    public TeamMember getById(String id) {
        return teamMemberRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("teamMember"));
    }

    public TeamMember getByUserAndTeam(String userId, String teamId) {
        return teamMemberRepository.getByUserAndTeam(userId, teamId);
    }

    public TeamMember save(TeamMember teamMember) {
        TeamMember existingTeamMember = getByUserAndTeam(teamMember.getUserId(), teamMember.getTeamId());
        if (existingTeamMember != null) {
            log.info("Team member {} already exists", existingTeamMember.getId());
            return existingTeamMember;
        }
        User user = userService.getById(teamMember.getUserId());
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember.toBuilder().name(user.getName()).build());
        log.info("Team member {} has been saved", savedTeamMember.getId());
        return savedTeamMember;
    }

    public String delete(String teamMemberId) {
        log.info("Removing team member {}", teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(() -> BadRequestException.resourceNotFound("teamMember"));
        if (teamMember.getRole() == MemberRole.LEADER) {
            log.error("Member {} is the team leader and cannot be removed", teamMember.getId());
            throw BadRequestException.invalidRequest();
        }

        User user = userService.getById(teamMember.getUserId());
        String soloTeamId = teamRepository.getStarterTeam(user.getId()).id();
        if (!user.getCurrentTeamId().equals(soloTeamId)) {
            user.setCurrentTeamId(teamRepository.getStarterTeam(user.getId()).id());
            userService.save(user);
        }
        stagerService.removeAllByTeamMemberId(teamMemberId);

        teamMemberRepository.delete(teamMemberId);
        return teamMember.getId();
    }

    public String removeTeamMember(TeamMember teamMember) {
        String teamMemberId = teamMember.getId();
        delete(teamMemberId);
        notifyRemovedUser(teamMember);
        return teamMemberId;
    }

    public List<TeamMember> getAllByTeam(String teamId, String userId, boolean includeCurrentUser) {
        return teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);
    }

    public TeamMember update(String id, TeamMember request) {
        TeamMember existingTeamMember = getById(id);
        MemberRole currentRole = existingTeamMember.getRole();
        log.info("Updating team member {} with request {}", existingTeamMember.getId(), request);

        existingTeamMember.setRole(request.getRole() != null ? request.getRole() : existingTeamMember.getRole());
        existingTeamMember.setInviteStatus(request.getInviteStatus() != null ? request.getInviteStatus() : existingTeamMember.getInviteStatus());
        teamMemberRepository.save(existingTeamMember);

        if (request.getInviteStatus() != PENDING) {
            notifyLeader(existingTeamMember);
        }
        if (currentRole != existingTeamMember.getRole()) {
            notifyTeamMemberWithNewRole(existingTeamMember);
        }
        return existingTeamMember;
    }

    public List<TeamMember> getAllUninvitedMembers(String eventId, String userId, String teamId, boolean includeCurrentUser) {
        final List<Stager> stagers = stagerService.getAllByEventId(eventId);
        final List<TeamMember> teamMembers = teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);

        return teamMembers.stream()
                .filter(teamMember -> teamMember.getInviteStatus() == CONFIRMED)
                .filter(member -> !stagers.stream().map(Stager::teamMemberId).toList().contains(member.getId()))
                .collect(Collectors.toList());
    }

    public TeamMember inviteMember(String email, MemberRole memberRole, String teamMemberInvited, String teamId, String invitedBy) {
        User invitedUser;
        TeamMember teamMember;
        Team team = teamRepository.findById(teamId).orElseThrow(() -> BadRequestException.resourceNotFound("team"));

        if (Strings.isNotEmpty(email)) {
            invitedUser = userService.getByEmail(email);

            if (invitedUser == null) {
                sendGridService.sendInviteToTeamEmail(email, team.name());
                return null;
            }

            teamMember = teamMemberRepository.getByUserAndTeam(invitedUser.getId(), teamId);
            if (teamMember == null) {
                teamMember = teamMemberRepository.save(
                        TeamMember.builder()
                                .teamId(teamId)
                                .userId(invitedUser.getId())
                                .role(memberRole)
                                .name(invitedUser.getName())
                                .inviteStatus(PENDING)
                                .build()
                );
            } else if (teamMember.getInviteStatus() == CONFIRMED) {
                throw BadRequestException.teamMemberAlreadyExists();
            }
        } else if (Strings.isNotEmpty(teamMemberInvited)) {
            teamMember = getById(teamMemberInvited);
            if (teamMember.getInviteStatus() == CONFIRMED) {
                throw BadRequestException.teamMemberAlreadyExists();
            }
            invitedUser = userService.getById(teamMember.getUserId());
        } else {
            throw BadRequestException.invalidRequest();
        }

        if (invitedUser == null) {
            throw BadRequestException.resourceNotFound("user");
        }

        notifyInvitedUser(team, invitedBy, invitedUser, teamMember.getId());
        return teamMember;
    }

    public Integer countByTeamId(String teamId) {
        return teamMemberRepository.countByTeamId(teamId);
    }

    public List<String> getMemberWithPhotoIds(String teamId) {
        return teamMemberRepository.getMemberWithPhotoIds(teamId);
    }

    public MemberRole getRole(Team team, String userId) {
        return teamMemberRepository.getByUserAndTeam(userId, team.id()).getRole();
    }

    public void updateTeamMembersIfNeeded(String planId, String teamId) {
        Plan plan = planService.getById(planId);
        Plan activePlan = planService.getActiveOrTrialPlan(teamId);
        if (!plan.getId().equals(activePlan.getId())) {
            throw BadRequestException.plansNotMatching();
        }
        int membersCount = countByTeamId(teamId);
        if (membersCount > plan.getMaxMembers()) {
            updateMembersState(teamId, membersCount - plan.getMaxMembers(), true);
        } else if (membersCount < plan.getMaxMembers()) {
            updateMembersState(teamId, plan.getMaxMembers() - membersCount, false);
        }
    }

    private void updateMembersState(String teamId, int limit, boolean isDowngrade) {
        List<TeamMember> teamMembersToUpdate = teamMemberRepository.getAllToUpdate(teamId, limit, isDowngrade ? CONFIRMED : INACTIVE);
        teamMembersToUpdate.forEach(teamMember -> {
            teamMemberRepository.save(teamMember.toBuilder().inviteStatus(isDowngrade ? INACTIVE : CONFIRMED).build());
            notifyRemovedUser(teamMember);
        });
    }


    private void notifyLeader(TeamMember teamMember) {
        if (teamMember.getInviteStatus() == CONFIRMED && teamMember.getRole() != MemberRole.LEADER) {
            Team team = teamRepository.findById(teamMember.getTeamId()).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
            String description = String.format("%s accepted your invitation to join %s", teamMember.getName(), team.name());
            notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_ACCEPTED, team.leaderId(), description, null, NotificationParams.builder().teamMemberId(teamMember.getId()).userId(teamMember.getUserId()).build());
        }

        if (teamMember.getInviteStatus() == DECLINED && teamMember.getRole() != MemberRole.LEADER) {
            Team team = teamRepository.findById(teamMember.getTeamId()).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
            delete(teamMember.getId());
            String description = String.format("%s declined your invitation to join %s", teamMember.getName(), team.name());
            notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_DECLINED, team.leaderId(), description, null, NotificationParams.builder().teamMemberId(teamMember.getId()).userId(teamMember.getUserId()).build());
        }
    }

    private void notifyInvitedUser(Team team, String invitedBy, User invitedUser, String teamMemberId) {
        User invitedByUser = userService.getById(invitedBy);
        String description = String.format("%s invited you to join %s team", invitedByUser.getName(), team.name());
        NotificationParams params = NotificationParams.builder().teamMemberId(teamMemberId).teamId(team.id()).build();
        notificationService.deleteNotification(NotificationType.TEAM_INVITATION_REQUEST, params);
        notificationService.sendNotificationToUser(NotificationType.TEAM_INVITATION_REQUEST, invitedUser.getId(), description, team.name(), params);
    }

    private void notifyRemovedUser(TeamMember teamMember) {
        Team team = teamRepository.findById(teamMember.getTeamId()).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
        String description = String.format("You have been removed from %s team", team.name());
        notificationService.sendNotificationToUser(NotificationType.TEAM_MEMBER_REMOVED, teamMember.getUserId(), description, null, NotificationParams.builder().build());
    }

    private void notifyTeamMemberWithNewRole(TeamMember teamMember) {
        Team team = teamRepository.findById(teamMember.getTeamId()).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
        String description;
        if (teamMember.getRole() == MemberRole.EDITOR) {
            description = String.format("You have been upgraded with Editor rights in %s team", team.name());
        } else {
            description = String.format("You have been downgraded from editor position in %s team", team.name());
        }
        notificationService.sendNotificationToUser(NotificationType.TEAM_MEMBER_REMOVED, teamMember.getUserId(), description, null, NotificationParams.builder().build());
    }
}

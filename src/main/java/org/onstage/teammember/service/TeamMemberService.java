package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.MemberRole;
import org.onstage.exceptions.BadRequestException;
import org.onstage.sendgrid.SendGridService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.onstage.team.service.TeamService;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.onstage.enums.MemberInviteStatus.PENDING;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;
    private final StagerService stagerService;
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final SendGridService sendGridService;

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
        teamRepository.changeMembersCount(teamMember.teamId(), 1);
        log.info("Team member {} has been saved", savedTeamMember.id());
        return savedTeamMember;
    }

    public String delete(String id) {
        TeamMember teamMember = teamMemberRepository.findById(id).orElseThrow(BadRequestException::teamMemberNotFound);
        log.info("Deleting team member {}", id);
        teamMemberRepository.delete(id);
        teamRepository.changeMembersCount(teamMember.teamId(), -1);
        return teamMember.id();
    }

    public List<TeamMember> getAllByTeam(String teamId, String userId, boolean includeCurrentUser) {
        return teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);
    }

    public TeamMember update(TeamMember existingTeamMember, TeamMember teamMember) {
        log.info("Updating team member {} with request {}", existingTeamMember.id(), teamMember);
        return save(
                teamMember.toBuilder()
                        .role(teamMember.role() != null ? teamMember.role() : existingTeamMember.role())
                        .build()
        );
    }

    public List<TeamMember> getAllUninvitedMembers(String eventId, String userId, String teamId, boolean includeCurrentUser) {
        final List<Stager> stagers = stagerService.getAllByEventId(eventId);
        final List<TeamMember> teamMembers = teamMemberRepository.getAllByTeam(teamId, userId, includeCurrentUser);

        return teamMembers.stream()
                .filter(member -> !stagers.stream().map(Stager::teamMemberId).toList().contains(member.id()))
                .collect(Collectors.toList());
    }

    public TeamMember inviteMember(String email, MemberRole memberRole, String teamId) {
        User user = userService.getByEmail(email);
        if (user == null) {
            throw BadRequestException.userNotFound();
        }
        TeamMember existingTeamMember = getByUserAndTeam(user.id(), teamId);
        if(existingTeamMember != null) {
            throw BadRequestException.userAlreadyInTeam();
        }

        Team team = teamService.getById(teamId);
        sendGridService.sendInviteToTeamEmail(user, team.name());

        return save(TeamMember.builder()
                .teamId(teamId)
                .userId(user.id())
                .role(memberRole)
                .name(user.name())
                .inviteStatus(PENDING)
                .build());
    }
}

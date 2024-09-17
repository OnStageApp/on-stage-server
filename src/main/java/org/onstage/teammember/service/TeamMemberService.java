package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;
    private final StagerService stagerService;

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
        teamMemberRepository.findById(id).orElseThrow(BadRequestException::teamMemberNotFound);
        log.info("Deleting team member {}", id);
        return teamMemberRepository.delete(id);
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
                .filter(member -> !stagers.stream().map(Stager::teamMemberId).toList().contains(member.userId()))
                .collect(Collectors.toList());
    }
}

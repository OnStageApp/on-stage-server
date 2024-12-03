package org.onstage.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.MemberRole;
import org.onstage.exceptions.BadRequestException;
import org.onstage.subscription.service.SubscriptionService;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.onstage.enums.MemberInviteStatus.CONFIRMED;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SubscriptionService subscriptionService;

    public Team getById(String id) {
        return teamRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
    }

    public Team create(Team team) {
        Team savedTeam = teamRepository.save(team);
        log.info("Team {} has been created", savedTeam.getId());
        teamMemberRepository.save(TeamMember.builder()
                .teamId(savedTeam.getId())
                .userId(savedTeam.getLeaderId())
                .role(MemberRole.LEADER)
                .inviteStatus(CONFIRMED).build());
        subscriptionService.createStarterSubscription(savedTeam.getId(), savedTeam.getLeaderId());
        return getById(savedTeam.getId());
    }

    public String delete(String id) {
        teamRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
        log.info("Deleting team {}", id);
        return teamRepository.delete(id);
    }

    public Team update(Team existingTeam, Team request) {
        log.info("Updating team {} with request {}", existingTeam.getId(), request);
        existingTeam.setName(request.getName() != null ? request.getName() : existingTeam.getName());
        return teamRepository.save(existingTeam);
    }

    public List<Team> getAll(String userId) {
        return teamRepository.getAll(userId);
    }

    public Team getStarterTeam(String id) {
        return teamRepository.getStarterTeam(id);
    }
}

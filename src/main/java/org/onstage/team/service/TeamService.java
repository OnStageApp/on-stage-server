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
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public Team getById(String id) {
        return teamRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
    }

    public Team create(Team team) {
        Team savedTeam = teamRepository.save(team);
        User user = userService.getById(savedTeam.leaderId());
        log.info("Team {} has been created", savedTeam.id());
        teamMemberRepository.save(TeamMember.builder()
                .teamId(savedTeam.id())
                .userId(savedTeam.leaderId())
                .name(user.getName() != null ? user.getName() : user.getEmail())
                .role(MemberRole.LEADER)
                .inviteStatus(CONFIRMED).build());
        subscriptionService.createStarterSubscription(savedTeam.id(), savedTeam.leaderId());
        return getById(savedTeam.id());
    }

    public String delete(String id) {
        teamRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("team"));
        log.info("Deleting team {}", id);
        return teamRepository.delete(id);
    }

    public Team update(Team existingTeam, TeamDTO request) {
        log.info("Updating team {} with request {}", existingTeam.id(), request);
        existingTeam = Team.builder()
                .id(existingTeam.id())
                .leaderId(existingTeam.leaderId())
                .name(request.name() != null? request.name() : existingTeam.name()).build();
        return teamRepository.save(existingTeam);
    }

    public List<Team> getAll(String userId) {
        return teamRepository.getAll(userId);
    }

    public Team getStarterTeam(String id) {
        return teamRepository.getStarterTeam(id);
    }
}

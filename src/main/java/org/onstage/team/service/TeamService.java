package org.onstage.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.MemberRole;
import org.onstage.exceptions.BadRequestException;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.onstage.teammember.service.TeamMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.onstage.enums.MemberInviteStatus.CONFIRMED;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Team getById(String id) {
        return teamRepository.findById(id).orElseThrow(BadRequestException::teamNotFound);
    }

    public Team save(Team team, String userId) {
        Team savedTeam = teamRepository.save(team);
        log.info("Team {} has been saved", savedTeam.id());
        teamMemberRepository.save(TeamMember.builder()
                .teamId(savedTeam.id())
                .userId(userId)
                .role(MemberRole.LEADER)
                .inviteStatus(CONFIRMED).build());
        return getById(savedTeam.id());
    }

    public String delete(String id) {
        teamRepository.findById(id).orElseThrow(BadRequestException::teamNotFound);
        log.info("Deleting team {}", id);
        return teamRepository.delete(id);
    }

    public Team update(Team existingTeam, TeamDTO request) {
        log.info("Updating team {} with request {}", existingTeam.id(), request);
        Team updatedTeam = updateTeamFromDTO(existingTeam, request);
        return teamRepository.save(updatedTeam);
    }

    private Team updateTeamFromDTO(Team existingTeam, TeamDTO request) {
        return Team.builder()
                .name(request.name() == null ? existingTeam.name() : request.name())
                .build();
    }

    public List<Team> getAll(String userId) {
        return teamRepository.getAll(userId);
    }
}

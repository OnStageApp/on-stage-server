package org.onstage.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.springframework.stereotype.Service;

import static org.onstage.exceptions.BadRequestException.teamNotFound;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public Team getById(String id) {
        return teamRepository.getById(id);
    }

    public Team save(Team team) {
        Team savedTeam = teamRepository.save(team);
        log.info("Team {} has been saved", savedTeam.id());
        return savedTeam;
    }

    public String delete(String id) {
        if (teamRepository.getById(id) == null) {
            throw teamNotFound();
        }
        log.info("Deleting team {}", id);
        return teamRepository.delete(id);
    }

    public Team update(Team existingTeam, TeamDTO request) {
        log.info("Updating team {} with request {}", existingTeam.id(), request);
        Team updatedTeam = updateTeamFromDTO(existingTeam, request);
        return save(updatedTeam);
    }

    private Team updateTeamFromDTO(Team existingTeam, TeamDTO request) {
        return Team.builder()
                .name(request.name() == null ? existingTeam.name() : request.name())
                .build();
    }
}
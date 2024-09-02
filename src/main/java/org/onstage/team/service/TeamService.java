package org.onstage.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public Team getById(String id) {
        Team team = teamRepository.getById(id);
        if (team == null) {
            throw BadRequestException.teamNotFound();
        }
        return team;
    }

    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public String delete(String id) {
        return teamRepository.delete(id);
    }

    public Team update(String id, TeamDTO request) {
        Team existingTeam = getById(id);
        Team updatedTeam = updateTeamFromDTO(existingTeam, request);
        return save(updatedTeam);
    }

    private Team updateTeamFromDTO(Team existingTeam, TeamDTO request) {
        return Team.builder()
                .name(request.name() == null ? existingTeam.name() : request.name())
                .build();
    }
}

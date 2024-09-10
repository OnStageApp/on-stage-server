package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getById(String id) {
        return teamMemberRepository.getById(id);
    }

    public TeamMember save(TeamMember teamMember) {
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);
        log.info("Team member {} has been saved", savedTeamMember.id());
        return savedTeamMember;
    }

    public String delete(String id) {
        if (teamMemberRepository.getById(id) == null) {
            throw BadRequestException.teamMemberNotFound();
        }
        log.info("Deleting team member {}", id);
        return teamMemberRepository.delete(id);
    }

    public List<TeamMember> getAllByTeam(String teamId) {
        return teamMemberRepository.getAllByTeam(teamId);
    }
}

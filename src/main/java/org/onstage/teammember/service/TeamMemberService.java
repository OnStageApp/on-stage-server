package org.onstage.teammember.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getById(String id) {
        TeamMember teamMember = teamMemberRepository.getById(id);
        if (teamMember == null) {
            throw BadRequestException.teamMemberNotFound();
        }
        return teamMember;
    }

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    public String delete(String id) {
        return teamMemberRepository.delete(id);
    }
}

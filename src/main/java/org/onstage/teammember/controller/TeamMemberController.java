package org.onstage.teammember.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.exceptions.BadRequestException;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.model.mapper.TeamMemberMapper;
import org.onstage.teammember.service.TeamMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.onstage.exceptions.BadRequestException.teamMemberNotFound;

@RestController
@RequestMapping("team-members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    private final TeamMemberMapper teamMemberMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDTO> getById(@PathVariable(name = "id") String id) {
        TeamMember teamMember = teamMemberService.getById(id);
        if (teamMember == null) {
            throw teamMemberNotFound();
        }
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMember));
    }

    @PostMapping
    public ResponseEntity<TeamMemberDTO> create(@RequestBody TeamMemberDTO request) {
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.save(teamMemberMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(teamMemberService.delete(id));
    }

}

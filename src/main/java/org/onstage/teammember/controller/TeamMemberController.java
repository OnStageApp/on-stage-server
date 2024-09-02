package org.onstage.teammember.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.model.mapper.TeamMemberMapper;
import org.onstage.teammember.service.TeamMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("team-members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    private final TeamMemberMapper teamMemberMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDTO> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.getById(id)));
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

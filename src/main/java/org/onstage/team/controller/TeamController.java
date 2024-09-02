package org.onstage.team.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.mapper.TeamMapper;
import org.onstage.team.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(teamMapper.toDto(teamService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO request) {
        return ResponseEntity.ok(teamMapper.toDto(teamService.save(teamMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(teamService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> update(@PathVariable String id, @RequestBody TeamDTO request) {
        return ResponseEntity.ok(teamMapper.toDto(teamService.update(id, request)));
    }
}

package org.onstage.team.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.team.client.CurrentTeamDTO;
import org.onstage.team.client.GetAllTeamsResponse;
import org.onstage.team.client.GetTeamResponse;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.model.mapper.TeamMapper;
import org.onstage.team.service.TeamService;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final UserSecurityContext userSecurityContext;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(teamMapper.toDto(teamService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<GetAllTeamsResponse> getAll() {
        String userId = userSecurityContext.getUserId();
        List<GetTeamResponse> teams = teamMapper.toDtoList(teamService.getAll(userId), userId);

        return ResponseEntity.ok(GetAllTeamsResponse.builder()
                .teams(teams)
                .currentTeamId(userService.getById(userId).currentTeamId())
                .build());

    }

    @GetMapping("/current")
    public ResponseEntity<CurrentTeamDTO> getCurrentTeam() {
        Team team = teamService.getById(userSecurityContext.getCurrentTeamId());
        return ResponseEntity.ok(teamMapper.toCurrentTeamDTO(team));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO request) {
        String userId = userSecurityContext.getUserId();
        return ResponseEntity.ok(teamMapper.toDto(teamService.save(teamMapper.toEntity(request), userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(teamService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> update(@PathVariable String id, @RequestBody TeamDTO request) {
        Team team = teamService.getById(id);
        return ResponseEntity.ok(teamMapper.toDto(teamService.update(team, request)));
    }
}

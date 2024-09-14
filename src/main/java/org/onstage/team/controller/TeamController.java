package org.onstage.team.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.team.client.GetAllTeamsResponse;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.model.mapper.TeamMapper;
import org.onstage.team.service.TeamService;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.onstage.exceptions.BadRequestException.teamNotFound;

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
        Team team = teamService.getById(id);
        if (team == null) {
            throw teamNotFound();
        }
        return ResponseEntity.ok(teamMapper.toDto(team));
    }

    @GetMapping
    public ResponseEntity<GetAllTeamsResponse> getAll() {
        String userId = userSecurityContext.getUserId();
        return ResponseEntity.ok(GetAllTeamsResponse.builder()
                .teams(teamMapper.toDtoList(teamService.getAll(userId)))
                .currentTeamId(userService.getById(userId).currentTeamId())
                .build());

    }

    @GetMapping("/current")
    public ResponseEntity<TeamDTO> getCurrentTeam() {
        String userId = userSecurityContext.getUserId();
        Team team = teamService.getById(userSecurityContext.getCurrentTeamId());
        return ResponseEntity.ok(teamMapper.toDto(team));
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
        Team team = teamService.getById(id);
        if (team == null) {
            throw teamNotFound();
        }
        return ResponseEntity.ok(teamMapper.toDto(teamService.update(team, request)));
    }
}

package org.onstage.team.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.team.client.GetAllTeamsResponse;
import org.onstage.team.client.TeamDTO;
import org.onstage.team.model.Team;
import org.onstage.team.model.mapper.TeamMapper;
import org.onstage.team.service.TeamService;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.service.TeamMemberService;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final TeamMapper teamMapper;
    private final UserSecurityContext userSecurityContext;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getById(@PathVariable String id) {
        Team team = teamService.getById(id);
        List<String> memberPhotoUrls = userService.getMembersPhotos(team.id());
        return ResponseEntity.ok(teamMapper.toDto(team).toBuilder().memberPhotoUrls(memberPhotoUrls).build());
    }

    @GetMapping
    public ResponseEntity<GetAllTeamsResponse> getAll() {
        String userId = userSecurityContext.getUserId();
        List<TeamDTO> teams = teamMapper.toDtoList(teamService.getAll(userId));
        
        return ResponseEntity.ok(GetAllTeamsResponse.builder()
                .teams(teams)
                .currentTeamId(userService.getById(userId).currentTeamId())
                .build());

    }

    @GetMapping("/current")
    public ResponseEntity<TeamDTO> getCurrentTeam() {
        Team team = teamService.getById(userSecurityContext.getCurrentTeamId());
        List<String> teamMembersUserIds = teamMemberService.getAllByTeam(team.id(), userSecurityContext.getUserId(), true).stream().map(TeamMember::userId).toList();
        teamMembersUserIds = teamMembersUserIds.stream().filter(userId -> userService.getById(userId).imageTimestamp() != null).limit(3).toList();
        return ResponseEntity.ok(teamMapper.toDto(team).toBuilder().membersUserIds(teamMembersUserIds).build());
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

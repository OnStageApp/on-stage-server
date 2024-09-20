package org.onstage.teammember.controller;

import com.amazonaws.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.teammember.client.GetTeamMembersResponse;
import org.onstage.teammember.client.InviteMemberDTO;
import org.onstage.teammember.client.TeamMemberDTO;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.model.mapper.TeamMemberMapper;
import org.onstage.teammember.service.TeamMemberService;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("team-members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    private final TeamMemberMapper teamMemberMapper;
    private final UserSecurityContext userSecurityContext;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDTO> getById(@PathVariable(name = "id") String id) {
        TeamMember teamMember = teamMemberService.getById(id);
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMember));
    }

    @GetMapping()
    public ResponseEntity<List<GetTeamMembersResponse>> getByTeam(@RequestParam(defaultValue = "true") boolean includeCurrentUser) {
        String teamId = userSecurityContext.getCurrentTeamId();
        String userId = userSecurityContext.getUserId();
        List<GetTeamMembersResponse> teamMembers = teamMemberMapper.toGetTeamMembersResponse(teamMemberService.getAllByTeam(teamId, userId, includeCurrentUser));
        teamMembers = teamMembers.stream()
                .map(teamMember -> teamMember.toBuilder().photoUrl(userService.generatePresignedUrl(teamMember.userId(), HttpMethod.GET)).build()).toList();
        return ResponseEntity.ok(teamMembers);
    }

    @GetMapping("/current")
    public ResponseEntity<TeamMemberDTO> getCurrentTeamMember() {
        String userId = userSecurityContext.getUserId();
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.getByUserAndTeam(userId, teamId)));
    }


    @PostMapping
    public ResponseEntity<TeamMemberDTO> create(@RequestBody TeamMemberDTO request) {
        request = request.toBuilder().teamId(userSecurityContext.getCurrentTeamId()).build();
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.save(teamMemberMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(teamMemberService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamMemberDTO> update(@PathVariable final String id, @RequestBody TeamMemberDTO request) {
        TeamMember existingTeamMember = teamMemberService.getById(id);
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.update(existingTeamMember, teamMemberMapper.toEntity(request))));
    }

    @GetMapping("/uninvited")
    public ResponseEntity<List<GetTeamMembersResponse>> getAllUninvitedMembers(@RequestParam final String eventId, @RequestParam(defaultValue = "true") boolean includeCurrentUser) {
        String userId = userSecurityContext.getUserId();
        String teamId = userSecurityContext.getCurrentTeamId();
        List<GetTeamMembersResponse> teamMembers = teamMemberMapper.toGetTeamMembersResponse(teamMemberService.getAllUninvitedMembers(eventId, userId, teamId, includeCurrentUser));
        teamMembers = teamMembers.stream()
                .map(teamMember -> teamMember.toBuilder().photoUrl(userService.generatePresignedUrl(teamMember.userId(), HttpMethod.GET)).build()).toList();
        return ResponseEntity.ok(teamMembers);
    }

    @PostMapping("/invite")
    public ResponseEntity<TeamMemberDTO> inviteMember(@RequestBody InviteMemberDTO request) {
        String teamId = userSecurityContext.getCurrentTeamId();
        TeamMember invitedTeamMember = teamMemberService.inviteMember(request.email(), request.newMemberRole(), teamId);
        return ResponseEntity.ok(teamMemberMapper.toDto(invitedTeamMember));
    }
}

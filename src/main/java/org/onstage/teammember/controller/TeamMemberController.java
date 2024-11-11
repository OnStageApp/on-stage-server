package org.onstage.teammember.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.plan.service.PlanService;
import org.onstage.teammember.client.GetTeamMemberPhoto;
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
    private final PlanService planService;

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
        return ResponseEntity.ok(teamMembers);
    }

    @GetMapping("/photos")
    public ResponseEntity<List<GetTeamMemberPhoto>> getMembersPhotos() {
        String teamId = userSecurityContext.getCurrentTeamId();
        String userId = userSecurityContext.getUserId();
        List<GetTeamMemberPhoto> teamMembersWithPhotos = teamMemberMapper.toTeamMemberPhotos(teamMemberService.getAllByTeam(teamId, userId, true));
        teamMembersWithPhotos = teamMembersWithPhotos.stream()
                .map(teamMember -> teamMember.toBuilder().photoUrl(userService.getPresignedUrl(teamMember.userId(), true)).build()).toList();
        return ResponseEntity.ok(teamMembersWithPhotos);
    }

    @GetMapping("/current")
    public ResponseEntity<TeamMemberDTO> getCurrentTeamMember() {
        String userId = userSecurityContext.getUserId();
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.getByUserAndTeam(userId, teamId)));
    }

    @PostMapping
    public ResponseEntity<TeamMemberDTO> create(@RequestBody TeamMemberDTO request) {
        String teamId = userSecurityContext.getCurrentTeamId();
//TODO: Uncomment
//        planService.checkPermission(PermissionType.ADD_TEAM_MEMBERS, teamId);
        request = request.toBuilder().teamId(teamId).build();
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.save(teamMemberMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(teamMemberService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamMemberDTO> update(@PathVariable final String id, @RequestBody TeamMemberDTO request) {
        return ResponseEntity.ok(teamMemberMapper.toDto(teamMemberService.update(id, teamMemberMapper.toEntity(request))));
    }

    @GetMapping("/uninvited")
    public ResponseEntity<List<GetTeamMembersResponse>> getAllUninvitedMembers(@RequestParam final String eventId, @RequestParam(defaultValue = "false") boolean includeCurrentUser) {
        String userId = userSecurityContext.getUserId();
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(teamMemberMapper.toGetTeamMembersResponse(teamMemberService.getAllUninvitedMembers(eventId, userId, teamId, includeCurrentUser)));
    }

    @PostMapping("/invite")
    public ResponseEntity<TeamMemberDTO> inviteMember(@RequestBody InviteMemberDTO request) {
        String teamId = userSecurityContext.getCurrentTeamId();
        String invitedBy = userSecurityContext.getUserId();
        TeamMember invitedTeamMember = teamMemberService.inviteMember(request.email(), request.newMemberRole(), request.teamMemberInvited(), teamId, invitedBy);
        return ResponseEntity.ok(teamMemberMapper.toDto(invitedTeamMember));
    }
}

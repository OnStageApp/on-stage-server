package org.onstage.teammember.client;

import org.onstage.enums.MemberRole;

public record InviteMemberDTO(String email, String teamMemberInvited, MemberRole newMemberRole) {
}

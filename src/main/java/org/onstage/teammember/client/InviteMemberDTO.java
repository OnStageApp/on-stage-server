package org.onstage.teammember.client;

import org.onstage.enums.MemberRole;

public record InviteMemberDTO(String emailOrUsername, String teamMemberInvited, MemberRole newMemberRole) {
}

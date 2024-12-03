package org.onstage.teammember.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.enums.MemberRole;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "teamMembers")
@Builder(toBuilder = true)
@FieldNameConstants
@Getter
@Setter
public class TeamMember extends BaseEntity {
    @MongoId
    private String id;
    private String userId;
    private String teamId;
    private MemberRole role;
    private MemberInviteStatus inviteStatus;
}

package org.onstage.teammember.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.MemberRight;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "team-members")
@Builder(toBuilder = true)
@FieldNameConstants
public record TeamMember(
        @MongoId
        String id,
        String userId,
        String teamId,
        MemberRight memberRight
) {
}

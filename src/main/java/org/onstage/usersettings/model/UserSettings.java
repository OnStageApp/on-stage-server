package org.onstage.usersettings.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.SongView;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Document("user-settings")
@FieldNameConstants
public record UserSettings(
        @MongoId
        String id,
        String userId,
        SongView songView,
        Boolean isDarkMode,
        Boolean isNotificationsEnabled

) {
}

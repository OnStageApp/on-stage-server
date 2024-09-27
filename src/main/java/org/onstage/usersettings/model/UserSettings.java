package org.onstage.usersettings.model;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.SongView;
import org.onstage.enums.TextSize;
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
        TextSize textSize,
        Boolean isDarkMode,
        Boolean isNotificationsEnabled,
        Boolean isOnboardingDone

) {
}

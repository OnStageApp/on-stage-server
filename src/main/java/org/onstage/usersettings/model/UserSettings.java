package org.onstage.usersettings.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.SongView;
import org.onstage.enums.TextSize;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Getter
@Setter
@Document("userSettings")
@FieldNameConstants
public class UserSettings extends BaseEntity {
        @MongoId
        String id;
        String userId;
        SongView songView;
        TextSize textSize;
        Boolean isDarkMode;
        Boolean isNotificationsEnabled;
        Boolean isOnboardingDone;
        Boolean isAddRemindersTooltipShown;
        Boolean isCreateEventTooltipShown;
}

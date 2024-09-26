package org.onstage.usersettings.client;

import lombok.Builder;
import org.onstage.enums.SongView;
import org.onstage.enums.TextSize;

@Builder
public record UserSettingsDTO(
        String userId,
        Boolean isDarkMode,
        Boolean isNotificationsEnabled,
        Boolean isOnboardingDone,
        SongView songView,
        TextSize textSize
        ) {
}

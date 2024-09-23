package org.onstage.usersettings.client;

import lombok.Builder;
import org.onstage.enums.SongView;

@Builder
public record UserSettingsDTO(
        String userId,
        Boolean isDarkMode,
        Boolean isNotificationsEnabled,
        SongView songView
        ) {
}

package org.onstage.usersettings.mapper;

import org.onstage.usersettings.client.UserSettingsDTO;
import org.onstage.usersettings.model.UserSettings;
import org.springframework.stereotype.Component;

@Component
public class UserSettingsMapper {
    public UserSettingsDTO toDTO(UserSettings userSettings) {
        return UserSettingsDTO.builder()
                .userId(userSettings.userId())
                .isDarkMode(userSettings.isDarkMode())
                .isNotificationsEnabled(userSettings.isNotificationsEnabled())
                .songView(userSettings.songView())
                .build();
    }

    public UserSettings toEntity(UserSettingsDTO userSettingsDTO) {
        return UserSettings.builder()
                .userId(userSettingsDTO.userId())
                .isDarkMode(userSettingsDTO.isDarkMode())
                .isNotificationsEnabled(userSettingsDTO.isNotificationsEnabled())
                .songView(userSettingsDTO.songView())
                .build();
    }
}

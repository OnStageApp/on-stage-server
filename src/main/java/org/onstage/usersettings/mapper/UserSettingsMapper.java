package org.onstage.usersettings.mapper;

import org.onstage.usersettings.client.UserSettingsDTO;
import org.onstage.usersettings.model.UserSettings;
import org.springframework.stereotype.Component;

@Component
public class UserSettingsMapper {
    public UserSettingsDTO toDTO(UserSettings userSettings) {
        return UserSettingsDTO.builder()
                .userId(userSettings.getUserId())
                .isDarkMode(userSettings.getIsDarkMode())
                .isNotificationsEnabled(userSettings.getIsNotificationsEnabled())
                .isOnboardingDone(userSettings.getIsOnboardingDone())
                .songView(userSettings.getSongView())
                .textSize(userSettings.getTextSize())
                .isAddRemindersTooltipShown(userSettings.getIsAddRemindersTooltipShown())
                .isCreateEventTooltipShown(userSettings.getIsCreateEventTooltipShown())
                .build();
    }

    public UserSettings toEntity(UserSettingsDTO userSettingsDTO) {
        return UserSettings.builder()
                .userId(userSettingsDTO.userId())
                .isDarkMode(userSettingsDTO.isDarkMode())
                .isNotificationsEnabled(userSettingsDTO.isNotificationsEnabled())
                .isOnboardingDone(userSettingsDTO.isOnboardingDone())
                .songView(userSettingsDTO.songView())
                .textSize(userSettingsDTO.textSize())
                .isCreateEventTooltipShown(userSettingsDTO.isCreateEventTooltipShown())
                .isAddRemindersTooltipShown(userSettingsDTO.isAddRemindersTooltipShown())
                .build();
    }
}

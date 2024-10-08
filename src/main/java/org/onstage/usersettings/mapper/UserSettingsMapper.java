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
                .isOnboardingDone(userSettings.isOnboardingDone())
                .songView(userSettings.songView())
                .textSize(userSettings.textSize())
                .isAddRemindersTooltipShown(userSettings.isAddRemindersTooltipShown())
                .isCreateEventTooltipShown(userSettings.isCreateEventTooltipShown())
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

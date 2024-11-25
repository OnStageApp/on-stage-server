package org.onstage.usersettings.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.SongView;
import org.onstage.enums.TextSize;
import org.onstage.exceptions.BadRequestException;
import org.onstage.usersettings.client.UserSettingsDTO;
import org.onstage.usersettings.model.UserSettings;
import org.onstage.usersettings.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;

    public UserSettings getUserSettings(String userId) {
        return userSettingsRepository.getUserSettings(userId);
    }

    public void createDefaultSettings(String userId) {
        UserSettings existingUserSettings = getUserSettings(userId);
        if (existingUserSettings != null) {
            return;
        }
        UserSettings savedUserSettings = userSettingsRepository.save(UserSettings.builder()
                .isDarkMode(false)
                .isNotificationsEnabled(true)
                .songView(SongView.AMERICAN)
                .textSize(TextSize.NORMAL)
                .isOnboardingDone(false)
                .isAddRemindersTooltipShown(false)
                .isCreateEventTooltipShown(false)
                .userId(userId)
                .build());

        log.info("Default user settings for user {} has been saved", savedUserSettings.getUserId());
    }

    public UserSettings update(String userId, UserSettings request) {
        UserSettings existingUserSettings = getUserSettings(userId);
        existingUserSettings.setIsDarkMode(request.getIsDarkMode() == null ? existingUserSettings.getIsDarkMode() : request.getIsDarkMode());
        existingUserSettings.setIsNotificationsEnabled(request.getIsNotificationsEnabled() == null ? existingUserSettings.getIsNotificationsEnabled() : request.getIsNotificationsEnabled());
        existingUserSettings.setSongView(request.getSongView() == null ? existingUserSettings.getSongView() : request.getSongView());
        existingUserSettings.setIsOnboardingDone(request.getIsOnboardingDone() == null ? existingUserSettings.getIsOnboardingDone() : request.getIsOnboardingDone());
        existingUserSettings.setTextSize(request.getTextSize() == null ? existingUserSettings.getTextSize() : request.getTextSize());
        existingUserSettings.setIsCreateEventTooltipShown(request.getIsCreateEventTooltipShown() == null ? existingUserSettings.getIsCreateEventTooltipShown() : request.getIsCreateEventTooltipShown());
        existingUserSettings.setIsAddRemindersTooltipShown(request.getIsAddRemindersTooltipShown() == null ? existingUserSettings.getIsAddRemindersTooltipShown() : request.getIsAddRemindersTooltipShown());

        return userSettingsRepository.save(existingUserSettings);
    }
}

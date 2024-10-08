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
            throw BadRequestException.userSettingsAlreadyCreated();
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

        log.info("Default user settings for user {} has been saved", savedUserSettings.userId());
    }

    public UserSettings update(String userId, UserSettingsDTO request) {
        UserSettings existingUserSettings = getUserSettings(userId);
        UserSettings updatedUserSettings = existingUserSettings.toBuilder()
                .isDarkMode(request.isDarkMode() == null ? existingUserSettings.isDarkMode() : request.isDarkMode())
                .isNotificationsEnabled(request.isNotificationsEnabled() == null ? existingUserSettings.isNotificationsEnabled() : request.isNotificationsEnabled())
                .songView(request.songView() == null ? existingUserSettings.songView() : request.songView())
                .isOnboardingDone(request.isOnboardingDone() == null ? existingUserSettings.isOnboardingDone() : request.isOnboardingDone())
                .textSize(request.textSize() == null ? existingUserSettings.textSize() : request.textSize())
                .isCreateEventTooltipShown(request.isCreateEventTooltipShown() == null ? existingUserSettings.isCreateEventTooltipShown() : request.isCreateEventTooltipShown())
                .isAddRemindersTooltipShown(request.isAddRemindersTooltipShown() == null ? existingUserSettings.isAddRemindersTooltipShown() : request.isAddRemindersTooltipShown())
                .build();
        return userSettingsRepository.save(updatedUserSettings);
    }
}

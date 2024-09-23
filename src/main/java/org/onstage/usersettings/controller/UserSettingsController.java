package org.onstage.usersettings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.usersettings.client.UserSettingsDTO;
import org.onstage.usersettings.mapper.UserSettingsMapper;
import org.onstage.usersettings.service.UserSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user-settings")
@RequiredArgsConstructor
@Slf4j
public class UserSettingsController {
    private final UserSettingsService userSettingsService;
    private final UserSettingsMapper userSettingsMapper;
    private final UserSecurityContext userSecurityContext;

    @GetMapping
    public ResponseEntity<UserSettingsDTO> getUserSettings() {
        String userId = userSecurityContext.getUserId();
        return ResponseEntity.ok(userSettingsMapper.toDTO(userSettingsService.getUserSettings(userId)));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserSettingsDTO> createDefaultSettings(@PathVariable(name = "userId") String userId) {
        log.info("Creating default user settings for user {}", userId);
        userSettingsService.createDefaultSettings(userId);
        return ResponseEntity.ok(userSettingsMapper.toDTO(userSettingsService.getUserSettings(userId)));
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateUserSettings(@RequestBody UserSettingsDTO request) {
        String userId = userSecurityContext.getUserId();
        log.info("Updating user settings for user {} with request {}", userId, request);
        return ResponseEntity.ok(userSettingsMapper.toDTO(userSettingsService.update(userId, request)));
    }


}

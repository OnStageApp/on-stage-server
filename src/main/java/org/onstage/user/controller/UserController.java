package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.user.client.UpdateUserRequest;
import org.onstage.user.client.UserDTO;
import org.onstage.user.client.UserProfileInfoDTO;
import org.onstage.user.model.mapper.UserMapper;
import org.onstage.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserSecurityContext userSecurityContext;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userMapper.toDtoList(userService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable final String id) {
        return ResponseEntity.ok(userMapper.toDto(userService.getById(id)));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userMapper.toDto(userService.getById(userSecurityContext.getUserId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") String id) {
        log.info("Deleting user {}", id);
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/photo/{userId}")
    public ResponseEntity<String> generateGetPresignedUrl(@PathVariable(name = "userId") String userId) {
        return ResponseEntity.ok(userService.getPresignedUrl(userId, false));
    }

    @GetMapping(value = "/photo/currentUser")
    public ResponseEntity<String> generateGetPresignedUrlForCurrentUser() {
        String userId = userSecurityContext.getUserId();
        return ResponseEntity.ok(userService.getPresignedUrl(userId, false));
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(@ModelAttribute MultipartFile image) {
        String userId = userSecurityContext.getUserId();
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            userService.uploadPhoto(userId, image.getBytes(), image.getContentType());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Error reading image file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userMapper.toDto(userService.update(userSecurityContext.getUserId(), request)));
    }

    @PostMapping("/team/{teamId}")
    public ResponseEntity<Void> setCurrentTeam(@PathVariable(name = "teamId") String teamId) {
        String userId = userSecurityContext.getUserId();
        userService.setCurrentTeam(teamId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile-info")
    public ResponseEntity<UserProfileInfoDTO> getProfileInfo(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(userMapper.toProfileInfoDTO(userService.getById(id)));
    }
}

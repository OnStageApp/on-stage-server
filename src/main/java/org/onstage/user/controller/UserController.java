package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
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

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userMapper.toDto(userService.save(userMapper.toEntity(user))));
    }

    @GetMapping(value = "/photo")
    public ResponseEntity<String> generateGetPresignedUrl() {
        String userId = userSecurityContext.getUserId();
        log.info("Get photo for user {}", userId);
        return ResponseEntity.ok(userService.getThumbnailPresignedUrl(userId));
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(@RequestParam("image") MultipartFile image) {
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

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO request) {
        User user = userService.getById(id);
        log.info("Updating user {} with request {}", id, request);
        return ResponseEntity.ok(userMapper.toDto(userService.update(user, request)));
    }

    @PostMapping("/team/{teamId}")
    public ResponseEntity<Void> setCurrentTeam(@PathVariable(name = "teamId") String teamId) {
        String userId = userSecurityContext.getUserId();
        userService.setCurrentTeam(teamId, userId);
        return ResponseEntity.ok().build();
    }
}

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

import static org.onstage.exceptions.BadRequestException.userNotFound;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserSecurityContext userSecurityContext;

    @GetMapping
    public List<UserDTO> getAll() {
        List<UserDTO> users = userMapper.toDtoList(userService.getAll());
        return users.stream().map(user -> user
                        .toBuilder()
                        .image(user.imageTimestamp() != null ? userService.getUserPhoto(user.id()) : null)
                        .build())
                .toList();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable final String id) {
        User user = userService.getById(id);
        return userMapper.toDto(user).toBuilder().image(user.imageTimestamp() != null ? userService.getUserPhoto(user.id()) : null).build();
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO user) {
        return userMapper.toDto(userService.save(userMapper.toEntity(user)));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(@PathVariable String id, @RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.uploadUserPhoto(id, image.getBytes(), image.getContentType());
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
        if (user == null) {
            throw userNotFound();
        }
        return ResponseEntity.ok(userMapper.toDto(userService.update(user, request)));
    }

    @PostMapping("/team/{teamId}")
    public ResponseEntity<Void> setCurrentTeam(@PathVariable(name = "teamId") String teamId) {
        String userId = userSecurityContext.getUserId();
        userService.setCurrentTeam(teamId, userId);
        return ResponseEntity.ok().build();
    }
}

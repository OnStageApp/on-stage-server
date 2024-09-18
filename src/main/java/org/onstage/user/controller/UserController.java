package org.onstage.user.controller;

import com.amazonaws.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.model.mapper.UserMapper;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/photoUrl")
    public ResponseEntity<String> generateGetPresignedUrl() {
        String userId = userSecurityContext.getUserId();
        log.info("Get photo for user {}", userId);
        return ResponseEntity.ok(userService.generatePresignedUrl(userId, HttpMethod.GET));
    }

    @PutMapping(value = "/photoUrl")
    public ResponseEntity<String> generatePutPresignedUrl() {
        String userId = userSecurityContext.getUserId();
        log.info("Upload photo for user {}", userId);
        return ResponseEntity.ok(userService.generatePresignedUrl(userId, HttpMethod.PUT));
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

package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.exceptions.BadRequestException;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.model.mapper.UserMapper;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.onstage.exceptions.BadRequestException.userNotFound;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAll() {
        return userMapper.toDtoList(userService.getAll());
    }

    @GetMapping("/uninvited")
    public List<UserDTO> getAllUninvitedUsers(@RequestParam final String eventId) {
        return userMapper.toDtoList(userService.getAllUninvitedUsers(eventId));
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable final String id) {
        return userMapper.toDto(userService.getById(id));
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO user) {
        return userMapper.toDto(userService.save(userMapper.toEntity(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO request) {
        User user = userService.getById(id);
        if (user == null) {
            throw userNotFound();
        }
        return ResponseEntity.ok(userMapper.toDto(userService.update(user, request)));
    }
}

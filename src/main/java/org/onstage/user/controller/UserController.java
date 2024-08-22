package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.User;
import org.onstage.user.model.mapper.UserMapper;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper mapper;
    private final UserService service;

    @GetMapping
    public List<User> getAll() {
        return mapper.toDtoList(service.getAll());
    }

    @GetMapping("/uninvited")
    public List<User> getAllUninvitedUsers(@RequestParam final String eventId) {
        return mapper.toDtoList(service.getAllUninvitedUsers(eventId));
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable final String id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return mapper.toDto(service.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody User request) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, request)));
    }
}

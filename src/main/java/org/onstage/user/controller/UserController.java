package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.UserDTO;
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
    public List<UserDTO> getAll() {
        return mapper.toDtoList(service.getAll());
    }

    @GetMapping("/uninvited")
    public List<UserDTO> getAllUninvitedUsers(@RequestParam final String eventId) {
        return mapper.toDtoList(service.getAllUninvitedUsers(eventId));
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable final String id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO user) {
        return mapper.toDto(service.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable String id, @RequestBody UserDTO request) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, request)));
    }
}

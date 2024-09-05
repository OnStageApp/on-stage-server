package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.UploadPhotoRequest;
import org.onstage.user.client.UserDTO;
import org.onstage.user.model.User;
import org.onstage.user.model.mapper.UserMapper;
import org.onstage.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
        List<UserDTO> users = userMapper.toDtoList(userService.getAll());
        return users.stream().map(user -> user
                        .toBuilder()
                        .image(user.imageTimestamp() != null ? userService.getUserPhoto(user.id()) : null)
                        .build())
                .toList();
    }

    @GetMapping("/uninvited")
    public List<UserDTO> getAllUninvitedUsers(@RequestParam final String eventId) {
        List<UserDTO> users = userMapper.toDtoList(userService.getAllUninvitedUsers(eventId));
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
    public ResponseEntity<Void> uploadPhoto(@ModelAttribute UploadPhotoRequest request) throws IOException {
        if (request.image() != null) {
            userService.uploadUserPhoto(request.id(), request.image().getBytes());
        }
        return ResponseEntity.ok().build();
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

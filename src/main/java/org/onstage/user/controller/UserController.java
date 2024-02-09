package org.onstage.user.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.onstage.user.client.User;
import org.onstage.user.model.mappers.UserMapper;
import org.onstage.user.service.UserService;
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
        return mapper.toApiList(service.getAll());
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable final String id) {
        return mapper.toApi(service.getById(id));
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return mapper.toApi(service.create(user));
    }

    @PatchMapping("/{id}")
    public User patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch) {
        return mapper.toApi(service.patch(id, jsonPatch));
    }
}

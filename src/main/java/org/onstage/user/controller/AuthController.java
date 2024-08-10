package org.onstage.user.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.user.client.LoginRequest;
import org.onstage.user.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public String authenticateUser(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}

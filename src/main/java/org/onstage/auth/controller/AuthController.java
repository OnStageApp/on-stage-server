package org.onstage.auth.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.onstage.auth.model.LoginRequest;
import org.onstage.auth.service.AuthService;
import org.onstage.exceptions.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest request) {
        try {
            return authService.login(request);
        } catch (FirebaseAuthException e) {
            throw BadRequestException.loginError(e.getMessage());
        }
    }
}

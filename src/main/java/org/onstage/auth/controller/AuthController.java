package org.onstage.auth.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.onstage.auth.model.LoginRequest;
import org.onstage.auth.model.TokenDTO;
import org.onstage.auth.model.TokenRefreshRequest;
import org.onstage.auth.service.AuthService;
import org.onstage.common.config.JwtTokenProvider;
import org.onstage.device.service.DeviceService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    private final DeviceService deviceService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public TokenDTO authenticateUser(@RequestBody LoginRequest request) {
        try {
            return authService.login(request);
        } catch (FirebaseAuthException e) {
            throw BadRequestException.loginError(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public TokenDTO refreshToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw BadRequestException.invalidRequest();
        }

        String email = jwtTokenProvider.getEmailFromJwt(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(BadRequestException::invalidRequest);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return TokenDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String deviceId) {
        deviceService.updateLoggedStatus(deviceId, false);
        return ResponseEntity.ok().build();
    }
}
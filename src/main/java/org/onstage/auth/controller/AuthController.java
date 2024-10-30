package org.onstage.auth.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.onstage.auth.model.LoginRequest;
import org.onstage.auth.service.AuthService;
import org.onstage.device.service.DeviceService;
import org.onstage.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    private final DeviceService deviceService;

    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest request) {
        try {
            return authService.login(request);
        } catch (FirebaseAuthException e) {
            throw BadRequestException.loginError(e.getMessage());
        }
    }

    @PostMapping("/logout/{deviceId}")
    public ResponseEntity<Void> logout(@PathVariable(name = "deviceId") String deviceId) {
        deviceService.updateLoggedStatus(deviceId, false);
        return ResponseEntity.ok().build();
    }
}

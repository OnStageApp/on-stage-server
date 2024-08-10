package org.onstage.user.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.common.config.JwtTokenProvider;
import org.onstage.user.client.LoginRequest;
import org.onstage.user.model.UserEntity;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @SneakyThrows
    public String login(LoginRequest request) {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.firebaseToken());
        String uid = decodedToken.getUid();

        UserEntity user = userRepository.findById(uid)
                .orElseGet(() -> createNewUser(uid, decodedToken));

        return jwtTokenProvider.generateToken(user);
    }

    private UserEntity createNewUser(String uid, FirebaseToken decodedToken) {
        return userRepository.save(UserEntity.builder()
                .id(uid)
                .name(decodedToken.getName())
                .email(decodedToken.getEmail())
                .build());
    }
}

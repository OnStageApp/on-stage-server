package org.onstage.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.onstage.auth.model.LoginRequest;
import org.onstage.common.config.JwtTokenProvider;
import org.onstage.exceptions.BadRequestException;
import org.onstage.team.model.Team;
import org.onstage.team.service.TeamService;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;

    private final static String SOLO_TEAM_NAME = "Solo Team";

    public String login(LoginRequest request) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.firebaseToken());

        if (decodedToken == null) {
            throw BadRequestException.firebaseTokenMissing();
        }

        String uid = decodedToken.getUid();

        User user = userRepository.findById(uid)
                .orElseGet(() -> createNewUser(uid, decodedToken));

        return jwtTokenProvider.generateToken(user);
    }

    private User createNewUser(String uid, FirebaseToken decodedToken) {
        User user = userRepository.save(User.builder()
                .id(uid)
                .name(decodedToken.getName())
                .email(decodedToken.getEmail())
                .build());
        Team team = teamService.save(Team.builder().name(SOLO_TEAM_NAME).build(), user.id());
        userRepository.save(user.toBuilder().currentTeamId(team.id()).build());
        return user;
    }
}

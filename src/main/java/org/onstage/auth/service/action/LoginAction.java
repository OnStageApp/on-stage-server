package org.onstage.auth.service.action;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.auth.model.LoginRequest;
import org.onstage.common.action.Action;
import org.onstage.common.config.JwtTokenProvider;
import org.onstage.exceptions.BadRequestException;
import org.onstage.team.model.Team;
import org.onstage.team.service.TeamService;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAction implements Action<LoginRequest, String> {
    private final static String SOLO_TEAM_NAME = "Solo Team";
    private final UserRepository userRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;

    @SneakyThrows
    @Override
    public String doExecute(LoginRequest request) {
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
        User user = userService.create(User.builder()
                .id(uid)
                .name(decodedToken.getName())
                .email(decodedToken.getEmail())
                .build());
        Team team = teamService.save(Team.builder().name(SOLO_TEAM_NAME).build(), user.id());
        return userRepository.save(user.toBuilder().currentTeamId(team.id()).build());

    }
}

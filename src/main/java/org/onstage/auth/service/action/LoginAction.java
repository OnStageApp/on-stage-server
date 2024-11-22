package org.onstage.auth.service.action;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.auth.model.LoginRequest;
import org.onstage.auth.model.TokenDTO;
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
public class LoginAction implements Action<LoginRequest, TokenDTO> {
    private final static String SOLO_TEAM_NAME = "Solo Team";
    private final UserRepository userRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final JwtTokenProvider jwtTokenProvider;

    @SneakyThrows
    @Override
    public TokenDTO doExecute(LoginRequest request) {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.firebaseToken());

        if (decodedToken == null) {
            throw BadRequestException.firebaseTokenMissing();
        }

        String uid = decodedToken.getUid();

        User user = userRepository.findById(uid)
                .orElseGet(() -> createNewUser(uid, decodedToken));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User createNewUser(String uid, FirebaseToken decodedToken) {
        String name;
        if (decodedToken.getName() == null) {
            name = decodedToken.getEmail().trim().split("@")[0];
        } else {
            name = decodedToken.getName();
        }
        User user = userService.create(User.builder()
                .id(uid)
                .name(name)
                .email(decodedToken.getEmail())
                .build());
        Team team = teamService.create(Team.builder().name(SOLO_TEAM_NAME).leaderId(user.getId()).build());
        return userRepository.save(user.toBuilder().currentTeamId(team.id()).build());

    }
}

package org.onstage.auth.service;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.onstage.auth.model.LoginRequest;
import org.onstage.auth.service.action.LoginAction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final LoginAction loginAction;

    public String login(LoginRequest request) throws FirebaseAuthException {
        return loginAction.execute(request);
    }
}

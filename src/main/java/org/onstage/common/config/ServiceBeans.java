package org.onstage.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.onstage.auth.config.SecurityConfig.extractJwtFromRequest;

@Configuration
@RequiredArgsConstructor
public class ServiceBeans implements WebMvcConfigurer {
    private final UserRepository userRepository;

    @Bean
    @RequestScope
    UserSecurityContext userSecurityContext() {
        return UserSecurityContext.builder()
                .build();
    }

    @Bean
    public HandlerInterceptor userSecurityInterceptor(UserSecurityContext userSecurityContext) {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String jwt = extractJwtFromRequest(request);
                Claims claim = Jwts.parser()
                        .setSigningKey("mySecret")
                        .parseClaimsJws(jwt)
                        .getBody();
                User currentUser = userRepository.findByEmail(claim.get("sub", String.class))
                        .orElseThrow(() -> new RuntimeException("User not found"));
                userSecurityContext.setUserId(currentUser.id());
                return true;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userSecurityInterceptor(userSecurityContext()));
    }
}

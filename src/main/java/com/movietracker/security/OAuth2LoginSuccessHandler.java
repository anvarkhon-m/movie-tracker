package com.movietracker.security;

import com.movietracker.config.AppProperties;
import com.movietracker.domain.User;
import com.movietracker.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Google OAuth muvaffaqiyatli yakunlanganda: foydalanuvchini topadi
 * (yo'q bo'lsa ro'yxatdan o'tkazadi), JWT yaratadi va frontend ga redirect qiladi.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String CALLBACK_PATH = "/auth/callback";

    private final AuthService authService;
    private final JwtService jwtService;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String googleId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String avatarUrl = oauth2User.getAttribute("picture");

        User user = authService.findOrCreateUser(googleId, email, name, avatarUrl);
        String token = jwtService.generateToken(user);

        String redirectUrl = UriComponentsBuilder.fromUriString(appProperties.frontendUrl())
                .path(CALLBACK_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
        response.sendRedirect(redirectUrl);
    }
}

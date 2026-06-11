package com.movietracker.controller;

import com.movietracker.dto.UserResponse;
import com.movietracker.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * /api/v1/auth/google/callback — Spring Security OAuth2 filter tomonidan
 * boshqariladi (SecurityConfig dagi redirectionEndpoint).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String GOOGLE_AUTHORIZATION_URI = "/oauth2/authorization/google";

    private final AuthService authService;

    @GetMapping("/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(GOOGLE_AUTHORIZATION_URI);
    }

    @GetMapping("/me")
    public UserResponse me() {
        return authService.getCurrentUserResponse();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        // JWT stateless — frontend tokenni o'chiradi, serverda sessiya yo'q
    }
}

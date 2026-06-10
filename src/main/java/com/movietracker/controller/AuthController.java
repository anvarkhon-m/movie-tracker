package com.movietracker.controller;

import com.movietracker.dto.UserResponse;
import com.movietracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/v1/auth/google va /api/v1/auth/google/callback
 * Spring Security OAuth2 filter lari tomonidan boshqariladi (SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

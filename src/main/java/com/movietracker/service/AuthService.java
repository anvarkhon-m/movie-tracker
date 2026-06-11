package com.movietracker.service;

import com.movietracker.domain.User;
import com.movietracker.dto.UserResponse;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.mapper.UserMapper;
import com.movietracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User findOrCreateUser(String googleId, String email, String name, String avatarUrl) {
        return userRepository.findByGoogleId(googleId)
                .map(user -> {
                    user.updateProfile(name, avatarUrl);
                    return user;
                })
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(user -> {
                            user.linkGoogleAccount(googleId, name, avatarUrl);
                            return user;
                        })
                        .orElseGet(() -> userRepository.save(User.builder()
                                .googleId(googleId)
                                .email(email)
                                .name(name)
                                .avatarUrl(avatarUrl)
                                .build())));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserResponse() {
        return userMapper.toResponse(getCurrentUser());
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Long userId = currentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new AccessDeniedException("Authentication required");
        }
        return userId;
    }
}

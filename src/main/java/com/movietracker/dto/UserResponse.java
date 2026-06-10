package com.movietracker.dto;

public record UserResponse(
        Long id,
        String email,
        String name,
        String avatarUrl
) {
}

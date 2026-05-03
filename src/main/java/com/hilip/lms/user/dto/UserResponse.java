package com.hilip.lms.user.dto;

public record UserResponse(
        String id,
        String username,
        String fullName,
        String email,
        String role
) {
}

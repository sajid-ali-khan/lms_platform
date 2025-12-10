package com.hilip.lms.dtos;

public record UserResponse(
        String id,
        String username,
        String fullName,
        String email,
        String role
) {
}

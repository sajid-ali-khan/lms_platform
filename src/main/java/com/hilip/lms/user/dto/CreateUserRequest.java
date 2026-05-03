package com.hilip.lms.user.dto;

public record CreateUserRequest(
        String fullName,
        String email,
        String password,
        String role
) {
}

package com.hilip.lms.dtos.user;

public record CreateUserRequest(
        String username,
        String fullName,
        String email,
        String password,
        String role
) {
}

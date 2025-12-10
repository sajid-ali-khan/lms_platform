package com.hilip.lms.dtos;

public record CreateUserRequest(
        String username,
        String fullName,
        String email,
        String password,
        String tenantId,
        String role
) {
}

package com.hilip.lms.dtos.auth;

import lombok.Data;

@Data
public final class JwtResponse {
    private final String accessToken;
    private final String type;
    private final String userId;
    private final String userEmail;
    private final String fullName;
    private final String role;
    private final String refreshToken;
    private final String tenantId;
    private final String tenantName;
}

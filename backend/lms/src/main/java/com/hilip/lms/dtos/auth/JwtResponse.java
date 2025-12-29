package com.hilip.lms.dtos.auth;

import lombok.Data;

@Data
public final class JwtResponse {
    private final String accessToken;
    private final String type;
    private final String username;
    private final String role;
    private final String refreshToken;
    private final String tenantId;
}

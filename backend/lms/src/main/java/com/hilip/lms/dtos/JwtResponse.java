package com.hilip.lms.dtos;

import lombok.Data;

@Data
public final class JwtResponse {
    private final String token;
    private final String type;
    private final String username;
    private final String role;
    private final String tenantId;
}

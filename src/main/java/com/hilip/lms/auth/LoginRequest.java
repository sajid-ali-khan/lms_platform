package com.hilip.lms.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

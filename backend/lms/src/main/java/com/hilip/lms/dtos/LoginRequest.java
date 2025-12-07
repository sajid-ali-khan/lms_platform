package com.hilip.lms.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

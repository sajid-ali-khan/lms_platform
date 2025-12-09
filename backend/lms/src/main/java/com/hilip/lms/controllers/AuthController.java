package com.hilip.lms.controllers;

import com.hilip.lms.dtos.JwtResponse;
import com.hilip.lms.dtos.LoginRequest;
import com.hilip.lms.exceptions.EmptyRequestBodyException;
import com.hilip.lms.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        if (loginRequest == null)
            throw new EmptyRequestBodyException();
        Optional<JwtResponse> jwtResponse;
        jwtResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtResponse.get());
    }
}

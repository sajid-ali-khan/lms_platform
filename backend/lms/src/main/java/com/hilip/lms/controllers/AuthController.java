package com.hilip.lms.controllers;

import com.hilip.lms.dtos.auth.JwtResponse;
import com.hilip.lms.dtos.auth.LoginRequest;
import com.hilip.lms.exceptions.EmptyRequestBodyException;
import com.hilip.lms.jwt.JwtUtils;
import com.hilip.lms.repositories.RefreshTokenRepository;
import com.hilip.lms.services.AuthService;
import com.hilip.lms.services.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        if (loginRequest == null)
            throw new EmptyRequestBodyException();
        Optional<JwtResponse> jwtResponse;
        jwtResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(jwtResponse.get());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");
        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    if (refreshTokenService.isTokenExpired(token)) {
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
                    }
                    String newJwt = jwtUtils.generateTokenFromUsername(token.getUser());
                    return ResponseEntity.ok(Map.of("token", newJwt));
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");

        if (requestToken == null || requestToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }

        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    refreshTokenRepository.delete(token);
                    return ResponseEntity.ok("Logged out successfully.");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }
}

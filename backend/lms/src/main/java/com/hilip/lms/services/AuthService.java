package com.hilip.lms.services;

import com.hilip.lms.dtos.auth.JwtResponse;
import com.hilip.lms.dtos.auth.LoginRequest;
import com.hilip.lms.exceptions.EmptyRequestBodyException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates a user and returns JWT tokens.
     * @param loginRequest the login credentials
     * @return JwtResponse containing access token, refresh token, and user info
     * @throws EmptyRequestBodyException if login request is null or has missing fields
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    public JwtResponse loginUser(LoginRequest loginRequest) {
        validateLoginRequest(loginRequest);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = (com.hilip.lms.models.User) authentication.getPrincipal();
        String jwt = jwtTokenService.generateToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId()).getToken();

        return new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getId().toString(),
                userDetails.getUsername(),
                userDetails.getFullName(),
                userDetails.getRole().name(),
                refreshToken,
                userDetails.getTenant() != null ? userDetails.getTenant().getId().toString() : null,
                userDetails.getTenant() != null ? userDetails.getTenant().getName() : null
        );
    }

    private void validateLoginRequest(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new EmptyRequestBodyException("Login request cannot be null.");
        }
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isBlank()) {
            throw new EmptyRequestBodyException("Username is required.");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            throw new EmptyRequestBodyException("Password is required.");
        }
    }
}

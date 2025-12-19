package com.hilip.lms.services;

import com.hilip.lms.dtos.auth.JwtResponse;
import com.hilip.lms.dtos.auth.LoginRequest;
import com.hilip.lms.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public Optional<JwtResponse> loginUser(LoginRequest loginRequest){
        var authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = (com.hilip.lms.models.User) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails);

        return Optional.of(new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getUsername(),
                userDetails.getRole().name(),
                userDetails.getTenant() != null ? userDetails.getTenant().getName() : null
        ));
    }
}

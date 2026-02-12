package com.hilip.lms.services;

import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.exceptions.TokenRefreshException;
import com.hilip.lms.jwt.JwtUtils;
import com.hilip.lms.models.RefreshToken;
import com.hilip.lms.models.User;
import com.hilip.lms.repositories.RefreshTokenRepository;
import com.hilip.lms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public RefreshTokenService(RefreshTokenRepository repo, UserRepository userRepo, JwtUtils jwtUtils) {
        this.refreshTokenRepository = repo;
        this.userRepository = userRepo;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);
        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    /**
     * Refreshes the JWT token using a valid refresh token.
     * Implements token rotation for security - generates new refresh token on each use.
     * @param refreshToken the refresh token string
     * @return Map containing the new JWT token and new refresh token
     * @throws TokenRefreshException if token is invalid or expired
     */
    @Transactional
    public Map<String, String> refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found."));

        if (isTokenExpired(token)) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token has expired. Please login again.");
        }

        String newJwt = jwtUtils.generateTokenFromUser(token.getUser());

        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenRepository.save(token);

        return Map.of(
                "accessToken", newJwt,
                "refreshToken", token.getToken()
        );
    }

    /**
     * Logs out the user by deleting their refresh token.
     * @param refreshToken the refresh token string
     * @return true if logout was successful, false if token was not found
     */
    @Transactional
    public boolean logout(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(token -> {
                    refreshTokenRepository.delete(token);
                    return true;
                })
                .orElse(false);
    }
}
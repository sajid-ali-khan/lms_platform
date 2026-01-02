package com.hilip.lms.repositories;

import com.hilip.lms.models.RefreshToken;
import com.hilip.lms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
    void deleteByExpiryDateBefore(Instant now);
}

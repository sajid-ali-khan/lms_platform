package com.hilip.lms.services;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long jwtExpirationMs;

    public JwtTokenService(
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationMs}") long jwtExpirationMs
    ) {
        this.jwtExpirationMs = jwtExpirationMs;
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("lms")
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpirationMs))
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}

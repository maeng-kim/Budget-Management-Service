package com.budget.infrastructure.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "jwt.duration", defaultValue = "86400")
    Long duration;

    public String generateToken(UUID userId, String email, String fullName) {
        return Jwt.issuer(issuer)
                .upn(email)
                .subject(userId.toString())
                .claim("userId", userId.toString())
                .claim("email", email)
                .claim("fullName", fullName)
                .groups(Set.of("User"))
                .expiresIn(Duration.ofSeconds(duration))
                .sign();
    }

    public String generateRefreshToken(UUID userId, String email) {
        return Jwt.issuer(issuer)
                .subject(userId.toString())
                .claim("userId", userId.toString())
                .claim("email", email)
                .groups(Set.of("Refresh"))
                .expiresIn(Duration.ofDays(30))
                .sign();
    }
}

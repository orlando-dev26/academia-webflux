package com.orlandini.academiawebflux.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.*;
import java.util.*;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final Duration expiration;
    public JwtUtil(@Value("${security.jwt.secret}") String secret,
                   @Value("${security.jwt.expiration:PT1H}") Duration expiration) {
        if (secret == null || secret.isBlank())
            throw new IllegalArgumentException("Configura JWT_SECRET en Base64 o ejecuta scripts/preparar-local.ps1");
        byte[] bytes;
        try { bytes = Base64.getDecoder().decode(secret); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("JWT_SECRET debe ser Base64 valido", e); }
        if (bytes.length < 32) throw new IllegalArgumentException("JWT_SECRET requiere al menos 32 bytes aleatorios");
        if (expiration.isNegative() || expiration.isZero())
            throw new IllegalArgumentException("JWT_EXPIRATION debe ser positiva");
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expiration = expiration;
    }
    public String generate(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plus(expiration)))
                .signWith(key, Jwts.SIG.HS256).compact();
    }
    public Claims validate(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        if (claims.getSubject() == null || claims.getSubject().isBlank() || claims.getExpiration() == null)
            throw new JwtException("Token incompleto");
        return claims;
    }
}

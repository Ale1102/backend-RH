package com.udb.rrhh.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // Clave JWT segura (mínimo 256 bits)
    @Value("${app.jwt.secret:myVerySecureSecretKeyThatIsAtLeast256BitsLongForJWTSecurity123456789}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:3600000}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        // Verificar que la clave tenga al menos 256 bits
        if (jwtSecret.getBytes().length < 32) { // 32 bytes = 256 bits
            logger.error("JWT Secret key is too short. Must be at least 256 bits (32 bytes)");
            throw new IllegalArgumentException("JWT Secret key must be at least 256 bits");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationMs);

            String roles = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            String token = Jwts.builder()
                    .setSubject(userPrincipal.getUsername())
                    .claim("roles", roles)
                    .claim("userId", userPrincipal.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            logger.info("✅ Token JWT generado exitosamente para usuario: {}", userPrincipal.getUsername());
            return token;

        } catch (Exception e) {
            logger.error("❌ Error generando token JWT: {}", e.getMessage());
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }
}

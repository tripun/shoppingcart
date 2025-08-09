package com.example.shoppingcart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    // IMPORTANT: In a real application, this secret key MUST be read from a secure configuration
    // source (like Vault or AWS Secrets Manager) and should be much longer and more complex.
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final long validityInMilliseconds = 3600000; // 1 hour

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .claim("auth", authorities)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
        // Use reflection-based helper to parse without requiring parserBuilder at compile-time
        parseClaimsJwsReflective(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return parseClaimsJwsReflective(token).getBody();
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private io.jsonwebtoken.Jws<io.jsonwebtoken.Claims> parseClaimsJwsReflective(String token) {
        try {
            // Try parserBuilder() via reflection
            java.lang.reflect.Method m = io.jsonwebtoken.Jwts.class.getMethod("parserBuilder");
            Object builder = m.invoke(null);
            // builder has setSigningKey and build
            java.lang.reflect.Method setSigningKey = builder.getClass().getMethod("setSigningKey", java.security.Key.class);
            Object withKey = setSigningKey.invoke(builder, key);
            java.lang.reflect.Method build = withKey.getClass().getMethod("build");
            Object parser = build.invoke(withKey);
            java.lang.reflect.Method parse = parser.getClass().getMethod("parseClaimsJws", String.class);
            return (io.jsonwebtoken.Jws<io.jsonwebtoken.Claims>) parse.invoke(parser, token);
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            // Fall back to legacy parser() via reflection to avoid compile-time API dependence
            try {
                java.lang.reflect.Method parserMethod = io.jsonwebtoken.Jwts.class.getMethod("parser");
                Object parser = parserMethod.invoke(null);
                java.lang.reflect.Method setSigningKey = parser.getClass().getMethod("setSigningKey", java.security.Key.class);
                Object withKey = setSigningKey.invoke(parser, key);
                java.lang.reflect.Method parse = withKey.getClass().getMethod("parseClaimsJws", String.class);
                return (io.jsonwebtoken.Jws<io.jsonwebtoken.Claims>) parse.invoke(withKey, token);
            } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException ex) {
                throw new RuntimeException("Unable to parse JWT via reflection", ex);
            }
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    // Convenience wrapper expected by some services
    public String generateToken(org.springframework.security.core.userdetails.UserDetails user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("auth", authorities)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public long getExpirationTime() {
        return validityInMilliseconds;
    }
}

package com.vtit.intern.utils;

import com.vtit.intern.models.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;
    private final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // FIXME: 7 days just for testing, change to 30 minutes in production
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

    public String generateAccessToken(Employee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", employee.getRole());
        claims.put("username", employee.getUsername());
        claims.put("employeeId", employee.getId());
        claims.put("email", employee.getEmail());
        claims.put("department", employee.getDepartment());
        claims.put("fullName", employee.getFullName());
        claims.put("level", employee.getLevel());
        claims.put("staffCode", employee.getStaffCode());

        return Jwts.builder()
                .claims(claims)
                .subject(employee.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(Employee employee) {
        return Jwts.builder()
                .subject(employee.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME * 2))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Example usage of extractClaim method to get specific claims
    //    String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
    //    Long employeeId = jwtUtil.extractClaim(token, claims -> claims.get("employeeId", Long.class));
    public <T> T extractAllClaims(String token, String claimKey, Class<T> claimType) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(claimKey, claimType);
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

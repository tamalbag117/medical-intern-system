package com.mims.medicalinternsystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // ✅ SECRET KEY
    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );
    }

    // ✅ GENERATE JWT WITH ROLES
    public String generateToken(
            UserDetails userDetails
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        // ✅ STORE ROLES
        List<String> roles =
                userDetails
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

        claims.put("roles", roles);

        return Jwts.builder()

                .setClaims(claims)

                .setSubject(
                        userDetails.getUsername()
                )

                .setIssuedAt(
                        new Date()
                )

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    // ✅ EXTRACT EMAIL
    public String extractEmail(
            String token
    ) {

        return extractAllClaims(token)
                .getSubject();
    }

    // ✅ EXTRACT ROLES
    public List<String> extractRoles(
            String token
    ) {

        Claims claims =
                extractAllClaims(token);

        return claims.get(
                "roles",
                List.class
        );
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(
            String token,
            UserDetails userDetails
    ) {

        final String email =
                extractEmail(token);

        return (
                email.equals(
                        userDetails.getUsername()
                ) &&
                        !isTokenExpired(token)
        );
    }

    // ✅ CHECK EXPIRY
    private boolean isTokenExpired(
            String token
    ) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // ✅ EXTRACT CLAIMS
    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parserBuilder()

                .setSigningKey(
                        getSigningKey()
                )

                .build()

                .parseClaimsJws(token)

                .getBody();
    }
}
package com.epharmacy.pharmacy_payment_service.configuration;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {

        return extractAllClaims(token).getSubject();
    }

    public Long extractCustomerId(String token) {

        return extractAllClaims(token)
                .get("id", Long.class);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {

        try {

            Claims claims = extractAllClaims(token);

            String username = claims.getSubject();
            Date expiration = claims.getExpiration();

            return username != null &&
                   expiration != null &&
                   expiration.after(new Date());

        } catch (Exception e) {

            return false;
        }
    }
}


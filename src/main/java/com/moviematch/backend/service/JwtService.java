package com.moviematch.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static  final String SECRET_KEY = "movie-match-super-secret-key-change-this-before-production-12345";
    private final SecretKey key ;
    public JwtService(){
        this.key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }
    public String generateToken(String email) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + 1000 * 60 * 60
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}

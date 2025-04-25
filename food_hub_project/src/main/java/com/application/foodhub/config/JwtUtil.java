package com.application.foodhub.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final Key key;
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // JWT 생성
    public String generateToken(String userId, String membershipType, String nickname) {
        String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8); // ✅ UTF-8 안전 인코딩
        return Jwts.builder()
                .setSubject(userId)
                .claim("membershipType", membershipType)
                .claim("nickname", encodedNickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // JWT 검증 및 파싱
    public String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
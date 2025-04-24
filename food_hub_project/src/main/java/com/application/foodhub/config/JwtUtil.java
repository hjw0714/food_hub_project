package com.application.foodhub.config;

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
    public String generateToken(String userId , String membershipType , String nickname) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("membershipType", membershipType) // 토큰에 권한(ADMIN or USER) 정보 추가
                .claim("nickname", nickname)
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
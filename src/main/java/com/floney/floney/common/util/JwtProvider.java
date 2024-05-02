package com.floney.floney.common.util;

import com.floney.floney.common.dto.Token;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenExpiredTime, refreshTokenExpiredTime;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisProvider redisProvider;

    public JwtProvider(@Value("${jwt.secret-key}") String secretKey,
                       @Value("${jwt.access-token.expired-time}") long accessTokenExpiredTime,
                       @Value("${jwt.refresh-token.expired-time}") long refreshTokenExpiredTime,
                       CustomUserDetailsService customUserDetailsService,
                       RedisProvider redisProvider) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenExpiredTime = accessTokenExpiredTime;
        this.refreshTokenExpiredTime = refreshTokenExpiredTime;
        this.customUserDetailsService = customUserDetailsService;
        this.redisProvider = redisProvider;
    }

    public Token generateToken(String subject) {
        return new Token(generateAccessToken(subject), generateRefreshToken(subject));
    }

    private String generateAccessToken(String subject) {
        Claims claims = Jwts.claims().setSubject(subject);

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + accessTokenExpiredTime);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiresIn)
            .signWith(key)
            .compact();
    }

    private String generateRefreshToken(String subject) {
        Claims claims = Jwts.claims().setSubject(subject);

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + refreshTokenExpiredTime);

        String refreshToken = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiresIn)
            .signWith(key)
            .compact();

        redisProvider.set(subject, refreshToken, refreshTokenExpiredTime);

        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }

    public String getUsername(final String token) {
        final Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody()
            .getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }
}

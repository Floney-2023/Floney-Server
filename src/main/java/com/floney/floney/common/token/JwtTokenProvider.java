package com.floney.floney.common.token;

import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.dto.security.UserDetail;
import com.floney.floney.user.service.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일
    private final Key key;
    private final CustomUserDetailService customUserDetailService;
    private final RedisProvider redisProvider;

    public JwtTokenProvider(@Value("${jwt.token.secret-key}") String secretKey,
                            CustomUserDetailService customUserDetailService,
                            RedisProvider redisProvider) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.customUserDetailService = customUserDetailService;
        this.redisProvider = redisProvider;
    }

    public Token generateToken(Authentication authentication) {
        return new Token(generateAccessToken(authentication), generateRefreshToken(authentication));
    }

    public String generateAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresIn)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiresIn)
                .signWith(key)
                .compact();

        redisProvider.set(authentication.getName(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        UserDetail userDetail = (UserDetail) customUserDetailService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
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
        } catch (IllegalArgumentException exception) {
            throw new JwtException("");
        }
    }

    public long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody()
                .getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

}

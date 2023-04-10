package com.floney.floney.user.service;

import com.floney.floney.common.BaseException;
import com.floney.floney.common.BaseResponseStatus;
import com.floney.floney.common.jwt.JwtTokenProvider;
import com.floney.floney.common.jwt.dto.TokenDto;
import com.floney.floney.user.dto.UserDto;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenDto login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                tokenDto.getRefreshToken(),
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );

        return tokenDto;
    }

    public String logout(String accessToken) throws BaseException {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();

        if (redisTemplate.opsForValue().get(email) != null) {
            redisTemplate.delete(email);
        }

        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        return email;
    }

    public void signup(UserDto userDto) throws BaseException {
        if (userRepository.existsById(userDto.getEmail())) {
            throw new BaseException(BaseResponseStatus.USER_EXIST);
        }

        User user = userDto.to();
        user.encodePassword(bCryptPasswordEncoder);

        userRepository.save(user);
    }

    public void signout(String email) {
        User user = userRepository.getReferenceById(email);
        user.signout();

        userRepository.save(user);
    }

    public TokenDto regenerateToken(TokenDto tokenDto) throws BaseException {
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String redisRefreshToken = redisTemplate.opsForValue().get(authentication.getName());

        if(!refreshToken.equals(redisRefreshToken)) {
            throw new BaseException(BaseResponseStatus.AUTHENTICATION_FAIL);
        }

        TokenDto newToken = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                tokenDto.getRefreshToken(),
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );

        return newToken;
    }

    public void updateNickname(String nickname) throws BaseException {
        User user = userRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST));
        user.updateNickname(nickname);
    }

    public void updatePassword(String password) throws BaseException {
        User user = userRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_EXIST));
        user.updatePassword(password);
        user.encodePassword(bCryptPasswordEncoder);
    }
}
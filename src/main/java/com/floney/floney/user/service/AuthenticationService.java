package com.floney.floney.user.service;

import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.CodeNotFoundException;
import com.floney.floney.common.exception.user.CodeNotSameException;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.common.util.RedisProvider;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.PasswordAuthenticateRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final UserRepository userRepository;
    private final RedisProvider redisProvider;
    private final MailProvider mailProvider;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Token login(final LoginRequest request) {
        // mysql read + write : 평균 120ms
        // mysql read + redis write : 평균 120ms ...
        try {
            final User user = findUserByEmail(request.getEmail());
            validatePasswordMatches(request, user.getPassword());

            redisProvider.set("*", "*", 1);
//            user.login();
//            userRepository.save(user);

            return jwtProvider.generateToken(user.getEmail());
        } catch (BadCredentialsException exception) {
            logger.warn("로그인 실패: [{}]", request.getEmail());
            throw exception;
        }
    }

    public String logout(final String accessToken) {
        jwtProvider.validateToken(accessToken);
        final String email = jwtProvider.getUsername(accessToken);

        if (redisProvider.get(email) != null) {
            redisProvider.delete(email);
        }

        final long expiration = jwtProvider.getExpiration(accessToken);
        redisProvider.set(accessToken, "logout", expiration);

        return email;
    }

    public Token reissueToken(Token token) {
        final String accessToken = token.getAccessToken();
        final String refreshToken = token.getRefreshToken();

        final String username = jwtProvider.getUsername(accessToken);
        final String redisRefreshToken = redisProvider.get(username);

        if (!refreshToken.equals(redisRefreshToken)) {
            throw new MalformedJwtException("");
        }

        return jwtProvider.generateToken(username);
    }

    public String sendEmailAuthMail(String email) {
        validateUserNotExistByEmail(email);

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        String code = String.format("%06d", random.nextInt(1_000_000) % 1_000_000);

        String mailSubject = "[Floney] 이메일 인증 코드";
        String mailText = String.format("인증 코드: %s\n앱으로 돌아가서 인증을 완료해주세요.\n", code);

        mailProvider.sendMail(email, mailSubject, mailText);
        redisProvider.set(email, code, 1000 * 60 * 5);
        return code;
    }

    public void authenticateEmail(EmailAuthenticationRequest emailAuthenticationRequest) {
        final String requestEmail = emailAuthenticationRequest.getEmail();
        final String requestCode = emailAuthenticationRequest.getCode();

        if (!redisProvider.hasKey(requestEmail)) {
            throw new CodeNotFoundException(requestEmail);
        }

        final String code = redisProvider.get(requestEmail);
        if (!code.equals(requestCode)) {
            throw new CodeNotSameException(code, requestCode);
        }
    }

    public void authenticatePassword(final String email, final PasswordAuthenticateRequest request) {
        final String password = request.getPassword();
        final User user = findUserByEmail(email);
        user.validateEmailUser();

        validatePasswordMatches(password, user);
    }

    private void validateUserNotExistByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }

    private User findUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private void validatePasswordMatches(final LoginRequest request, final String password) {
        if (!passwordEncoder.matches(request.getPassword(), password)) {
            throw new BadCredentialsException(request.getEmail());
        }
    }

    private void validatePasswordMatches(final String password, final User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(user.getEmail());
        }
    }
}

package com.floney.floney.user.service;

import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.exception.user.UserSignoutException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.user.client.KakaoClient;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoUserService implements OAuthUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final KakaoClient kakaoClient;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkIfSignup(String oAuthToken) {
        return userRepository.existsByProviderId(getProviderId(oAuthToken));
    }

    @Override
    @Transactional
    public Token signup(String oAuthToken, SignupRequest request) {
        validateIfNewUser(request.getEmail());

        String providerId = getProviderId(oAuthToken);
        User user = request.to(Provider.KAKAO, providerId);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        return generateToken(user);
    }

    @Override
    public Token login(String oAuthToken) {
        String providerId = getProviderId(oAuthToken);

        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UserNotFoundException(oAuthToken));

        return generateToken(user);
    }

    private Token generateToken(final User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), "auth")
            );

            return jwtProvider.generateToken(authentication);
        } catch (BadCredentialsException exception) {
            logger.warn("카카오 로그인 실패: [{}]", user.getEmail());
            throw exception;
        } catch (AccountStatusException exception) {
            logger.error("카카오 로그인 오류: {}", exception.getMessage());
            throw exception;
        }
    }

    private String getProviderId(String oAuthToken) {
        return kakaoClient.getAuthId(oAuthToken);
    }

    private void validateIfNewUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isInactive()) {
                throw new UserSignoutException();
            }
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }
}

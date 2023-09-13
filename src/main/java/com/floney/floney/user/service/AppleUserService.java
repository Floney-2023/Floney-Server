package com.floney.floney.user.service;

import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.exception.user.UserSignoutException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.user.client.AppleClient;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppleUserService implements OAuthUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final AppleClient appleClient;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkIfSignup(final String oAuthToken) {
        return userRepository.existsByProviderId(getProviderId(oAuthToken));
    }

    @Override
    @Transactional
    public Token signup(final String oAuthToken, final SignupRequest request) {
        validateUserExistByEmail(request.getEmail());

        final String providerId = getProviderId(oAuthToken);
        validateUserExistByProviderId(providerId);

        final User user = request.to(Provider.APPLE, providerId);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        return generateToken(user);
    }

    @Override
    public Token login(final String oAuthToken) {
        final String providerId = getProviderId(oAuthToken);

        final User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new UserNotFoundException(oAuthToken));

        return generateToken(user);
    }

    private Token generateToken(final User user) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), "auth")
            );

            return jwtProvider.generateToken(authentication);
        } catch (BadCredentialsException exception) {
            logger.warn("애플 로그인 실패: [{}]", user.getEmail());
            throw exception;
        } catch (AccountStatusException exception) {
            logger.error("애플 로그인 오류: {}", exception.getMessage());
            throw exception;
        }
    }

    private String getProviderId(String oAuthToken) {
        return appleClient.getAuthId(oAuthToken);
    }

    private void validateUserExistByProviderId(final String providerId) {
        userRepository.findByProviderId(providerId).ifPresent(user -> {
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }

    private void validateUserExistByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isInactive()) {
                throw new UserSignoutException();
            }
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }
}

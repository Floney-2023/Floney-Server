package com.floney.floney.user.service;

import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.UserFoundException;
import com.floney.floney.common.exception.user.NotFoundUserException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.user.client.KakaoClient;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoUserService implements OAuthUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final KakaoClient kakaoClient;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkIfSignup(String oAuthToken) {
        return userRepository.existsByProviderId(getProviderId(oAuthToken));
    }

    @Override
    @Transactional
    public Token signup(final String oAuthToken, final SignupRequest request) {
        final String providerId = getProviderId(oAuthToken);
        validateNewUser(request.getEmail());

        final User user = createUser(request, providerId);
        return generateToken(user.getEmail());
    }

    @Override
    @Transactional
    public Token login(final String oAuthToken) {
        final String providerId = getProviderId(oAuthToken);
        final User user = findUserByProviderId(oAuthToken, providerId);

        user.login();
        userRepository.save(user);

        return generateToken(user.getEmail());
    }

    private User createUser(final SignupRequest request, final String providerId) {
        final User user = request.to(Provider.KAKAO, providerId);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
        return user;
    }

    private Token generateToken(final String username) {
        return jwtProvider.generateToken(username);
    }

    private User findUserByProviderId(final String oAuthToken, final String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new NotFoundUserException(oAuthToken));
    }

    private String getProviderId(String oAuthToken) {
        return kakaoClient.getAuthId(oAuthToken);
    }

    private void validateNewUser(final String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserFoundException(user.getEmail(), user.getProvider());
        });
    }
}

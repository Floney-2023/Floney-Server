package com.floney.floney.user.service;

import com.floney.floney.common.exception.UserNotFoundException;
import com.floney.floney.common.token.JwtProvider;
import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.client.GoogleClient;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleUserService implements OAuthUserService {

    private final GoogleClient googleClient;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    public boolean checkIfSignup(String oAuthToken) {
        return userRepository.existsByProviderId(getProviderId(oAuthToken));
    }

    @Override
    @Transactional
    public void signup(String oAuthToken, SignupRequest request) {
        String providerId = getProviderId(oAuthToken);
        userRepository.save(request.to(Provider.GOOGLE.getName(), providerId));
    }

    @Override
    public Token login(String oAuthToken) {
        String providerId = getProviderId(oAuthToken);

        User user = userRepository.findByProviderId(providerId).orElseThrow(UserNotFoundException::new);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), "auth")
        );

        return jwtProvider.generateToken(authentication);
    }

    private String getProviderId(String oAuthToken) {
        googleClient.init(oAuthToken);
        return googleClient.getId();
    }

}

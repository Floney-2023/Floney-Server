package com.floney.floney.user.service;

import com.floney.floney.common.exception.UserNotFoundException;
import com.floney.floney.common.token.JwtProvider;
import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.client.GoogleClient;
import com.floney.floney.user.client.KakaoClient;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
    public void signup(String oAuthToken, SignupRequest request) {
        Long providerId = getProviderId(oAuthToken);
        userRepository.save(request.to(providerId));
    }

    @Override
    public Token login(String oAuthToken) {
        Long providerId = getProviderId(oAuthToken);

        User user = userRepository.findByProviderId(providerId).orElseThrow(UserNotFoundException::new);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );

        return jwtProvider.generateToken(authentication);
    }

    private Long getProviderId(String oAuthToken) {
        googleClient.init(oAuthToken);
        return googleClient.getId();
    }

}

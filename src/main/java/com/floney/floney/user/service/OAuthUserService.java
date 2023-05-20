package com.floney.floney.user.service;

import com.floney.floney.common.token.JwtProvider;
import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.client.KakaoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthUserService {

    private final KakaoClient kakaoClient;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public Token kakaoLogin(String oAuthToken) {
        String email = kakaoClient.getEmail(oAuthToken);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, null)
        );
        return jwtProvider.generateToken(authentication);
    }

}

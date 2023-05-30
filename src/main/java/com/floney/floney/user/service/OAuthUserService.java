package com.floney.floney.user.service;

import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.dto.request.SignupRequest;

public interface OAuthUserService {

    boolean checkIfSignup(String oAuthToken);
    void signup(String oAuthToken, SignupRequest request);
    Token login(String oAuthToken);

}

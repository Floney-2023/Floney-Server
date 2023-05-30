package com.floney.floney.user.controller;

import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/kakao")
@RequiredArgsConstructor
public class KakaoController implements AuthController {

    private final KakaoUserService kakaoUserService;

    @Override
    @GetMapping("/check")
    public ResponseEntity<?> checkIfSignup(String token) {
        return new ResponseEntity<>(kakaoUserService.checkIfSignup(token), HttpStatus.OK);
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(String token, SignupRequest request) {
        kakaoUserService.signup(token, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/login")
    public ResponseEntity<?> login(String token) {
        return new ResponseEntity<>(kakaoUserService.login(token), HttpStatus.OK);
    }

}

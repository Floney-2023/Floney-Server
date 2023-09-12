package com.floney.floney.user.controller;

import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.service.AppleUserService;
import com.floney.floney.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/apple")
public class AppleController implements AuthController {

    private final AppleUserService appleUserService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    @GetMapping("/check")
    public ResponseEntity<?> checkIfSignup(final String token) {
        return ResponseEntity.ok(appleUserService.checkIfSignup(token));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(final String token, final SignupRequest request) {
        customUserDetailsService.validateIfNewUser(request.getEmail());
        appleUserService.signup(token, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(final String token) {
        return ResponseEntity.ok(appleUserService.login(token));
    }
}

package com.floney.floney.user.controller;

import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.service.AppleUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/apple")
public class AppleController implements AuthController {

    private final AppleUserService appleUserService;

    @Override
    @GetMapping("/check")
    public ResponseEntity<?> checkIfSignup(final String token) {
        return ResponseEntity.ok(appleUserService.checkIfSignup(token));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(final String token, @Valid final SignupRequest request) {
        return new ResponseEntity<>(appleUserService.signup(token, request), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/login")
    public ResponseEntity<?> login(final String token) {
        return ResponseEntity.ok(appleUserService.login(token));
    }
}

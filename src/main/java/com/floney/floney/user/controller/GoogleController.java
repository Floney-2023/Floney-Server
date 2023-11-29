package com.floney.floney.user.controller;

import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.service.GoogleUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth/google")
@RequiredArgsConstructor
public class GoogleController implements AuthController {

    private final GoogleUserService googleUserService;

    @Override
    @GetMapping("/check")
    public ResponseEntity<?> checkIfSignup(String token) {
        return new ResponseEntity<>(googleUserService.checkIfSignup(token), HttpStatus.OK);
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(String token, @Valid SignupRequest request) {
        return new ResponseEntity<>(googleUserService.signup(token, request), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/login")
    public ResponseEntity<?> login(String token) {
        return new ResponseEntity<>(googleUserService.login(token), HttpStatus.OK);
    }

}

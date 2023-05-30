package com.floney.floney.user.controller;

import com.floney.floney.user.dto.request.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthController {

    ResponseEntity<?> checkIfSignup(@RequestParam String token);
    ResponseEntity<?> signup(@RequestParam String token, @RequestBody SignupRequest request);
    ResponseEntity<?> login(@RequestParam String token);

}

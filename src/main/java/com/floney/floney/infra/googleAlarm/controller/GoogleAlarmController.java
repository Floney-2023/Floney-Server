package com.floney.floney.infra.googleAlarm.controller;

import com.floney.floney.infra.googleAlarm.service.GoogleAlarmService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/google/alarm")
@RequiredArgsConstructor
public class GoogleAlarmController {

    private final GoogleAlarmService googleAlarmService;

    @GetMapping("/tokens")
    public ResponseEntity<?> generateToken(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(googleAlarmService.generateToken(userDetails.getUser()));
    }

}

package com.floney.floney.alarm.controller;

import com.floney.floney.alarm.service.AlarmService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/tokens")
    public ResponseEntity<?> generateToken(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(alarmService.generateToken(userDetails.getUser()));
    }
}

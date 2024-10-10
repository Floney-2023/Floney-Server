package com.floney.floney.subscribe.controller;

import com.floney.floney.user.client.AppleClient;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final AppleClient appleClient;

    @GetMapping("/transaction/apple")
    public ResponseEntity<?> getTransaction(@RequestParam String transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        return new ResponseEntity<>(appleClient.getTransaction(transactionId, userDetails.getUsername()), HttpStatus.OK);
    }
}

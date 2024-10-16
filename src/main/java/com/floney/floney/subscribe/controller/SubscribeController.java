package com.floney.floney.subscribe.controller;

import com.apple.itunes.storekit.model.ResponseBodyV2;
import com.apple.itunes.storekit.verification.VerificationException;
import com.floney.floney.user.client.AppleClient;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final AppleClient appleClient;

    @GetMapping("/apple/transaction")
    public ResponseEntity<?> getTransaction(@RequestParam String transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        return new ResponseEntity<>(appleClient.getTransaction(transactionId, userDetails.getUser()), HttpStatus.OK);
    }

    @PostMapping("/apple/notification")
    public ResponseEntity<?> callbackApple(ResponseBodyV2 responseBodyV2) throws VerificationException, IOException {
        appleClient.callback(responseBodyV2);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

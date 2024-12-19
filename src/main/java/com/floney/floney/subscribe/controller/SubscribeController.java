package com.floney.floney.subscribe.controller;

import com.apple.itunes.storekit.model.ResponseBodyV2;
import com.apple.itunes.storekit.verification.VerificationException;
import com.floney.floney.subscribe.Device;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.subscribe.dto.GoogleCallbackDto;
import com.floney.floney.user.client.AndroidClient;
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
    private final AndroidClient androidClient;

    @GetMapping("/apple/transaction")
    public ResponseEntity<?> getTransaction(@RequestParam String transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        return new ResponseEntity<>(appleClient.getTransaction(transactionId, userDetails.getUser()), HttpStatus.OK);
    }

    @PostMapping("/apple/notification")
    public ResponseEntity<?> callbackApple(@RequestBody ResponseBodyV2 responseBodyV2) throws VerificationException, IOException {
        appleClient.callback(responseBodyV2);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/android/transaction")
    public ResponseEntity<?> getAndroidTransaction(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String transactionId) throws IOException {
        return new ResponseEntity<>(androidClient.getTransaction(userDetails.getUser(), transactionId), HttpStatus.OK);
    }

    @PostMapping("/android/notification")
    public ResponseEntity<?> callbackAndroid(@RequestBody GoogleCallbackDto payload) throws VerificationException, IOException {
        androidClient.callback(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public GetTransactionResponse isSubscribe(@RequestHeader("device") String device, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (device.equals(Device.ANDROID.value)) {
            return androidClient.isSubscribe(userDetails.getUser());
        } else {
            return appleClient.isSubscribe(userDetails.getUser());
        }
    }
}

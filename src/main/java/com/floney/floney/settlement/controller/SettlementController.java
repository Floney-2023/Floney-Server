package com.floney.floney.settlement.controller;

import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.service.SettlementService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping("")
    public ResponseEntity<?> createSettlement(@RequestBody @Valid SettlementRequest request,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(settlementService.create(request, userDetails), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<?> getSettlementsByBook(@RequestParam Long bookId) {
        return new ResponseEntity<>(settlementService.findAll(bookId), HttpStatus.OK);
    }
}

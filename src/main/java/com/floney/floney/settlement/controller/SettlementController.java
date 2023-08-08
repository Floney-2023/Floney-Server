package com.floney.floney.settlement.controller;

import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.service.SettlementService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 정산 내역 생성
     * @param request 정산 내역 생성 요청
     * @return 생성한 정산 내역 응답
     */
    @PostMapping("")
    public ResponseEntity<?> createSettlement(@RequestBody @Valid SettlementRequest request) {
        return new ResponseEntity<>(settlementService.create(request), HttpStatus.CREATED);
    }

    /**
     * 가계부의 모든 정산 내역 조회
     * @param bookKey 가계부 key
     * @return 정산 내역 리스트
     */
    @GetMapping("")
    public ResponseEntity<?> getSettlementsByBook(@RequestParam String bookKey) {
        return new ResponseEntity<>(settlementService.findAll(bookKey), HttpStatus.OK);
    }

    /**
     * 정산 내역 조회
     * @param id 정산 내역 id(pk)
     * @return 요청한 정산 내역 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSettlement(@PathVariable Long id) {
        return new ResponseEntity<>(settlementService.find(id), HttpStatus.OK);
    }
}

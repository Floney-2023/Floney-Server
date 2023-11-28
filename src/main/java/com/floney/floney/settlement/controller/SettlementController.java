package com.floney.floney.settlement.controller;

import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.floney.floney.settlement.dto.response.SettlementResponse;
import com.floney.floney.settlement.service.SettlementService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 정산 내역 생성
     *
     * @param request 정산 내역 생성 요청
     * @return 생성한 정산 내역 응답
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SettlementResponse createSettlement(@RequestBody @Valid final SettlementRequest request) {
        return settlementService.create(request);
    }

    /**
     * 가계부의 모든 정산 내역 조회
     *
     * @param bookKey 가계부 key
     * @return 정산 내역 리스트
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SettlementResponse> getSettlementsByBook(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                         @RequestParam final String bookKey) {
        return settlementService.findAll(userDetails.getUsername(), bookKey);
    }

    /**
     * 정산 내역 조회
     *
     * @param id 정산 내역 id(pk)
     * @return 요청한 정산 내역 응답
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SettlementResponse getSettlement(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                            @PathVariable final Long id) {
        return settlementService.find(userDetails.getUsername(), id);
    }
}

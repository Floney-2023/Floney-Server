package com.floney.floney.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@RequiredArgsConstructor
public class SubscribeRequest {

    @NotNull(message = "originalTransactionId를 입력해주세요")
    private String originalTransactionId;

    @NotNull(message = "transactionId를 입력해주세요")
    private String transactionId;

    @NotNull(message = "productId를 입력해주세요")
    private String productId;

    @NotNull(message = "expiresDate를 입력해주세요")
    private String expiresDate;

    @NotNull(message = "subscriptionStatus를 입력해주세요")
    private String subscriptionStatus;

    @NotNull(message = "renewalStatus를 입력해주세요")
    private boolean renewalStatus;

    @Builder
    private SubscribeRequest(String originalTransactionId, String transactionId, String productId, String expiresDate, String subscriptionStatus, boolean renewalStatus) {
        this.originalTransactionId = originalTransactionId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.expiresDate = expiresDate;
        this.subscriptionStatus = subscriptionStatus;
        this.renewalStatus = renewalStatus;
    }

}

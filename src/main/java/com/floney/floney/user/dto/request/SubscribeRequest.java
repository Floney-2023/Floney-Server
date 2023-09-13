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

    @NotNull
    private String originalTransactionId;

    @NotNull
    private String transactionId;

    @NotNull
    private String productId;

    @NotNull
    private String expiresDate;

    @NotNull
    private String subscriptionStatus;

    @NotNull
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

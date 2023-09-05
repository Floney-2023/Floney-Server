package com.floney.floney.user.dto.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
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
}

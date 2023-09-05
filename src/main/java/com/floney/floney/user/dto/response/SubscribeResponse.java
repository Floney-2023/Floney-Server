package com.floney.floney.user.dto.response;

import com.floney.floney.user.entity.Subscribe;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
public class SubscribeResponse {

    private final String originalTransactionId;

    private final String transactionId;

    private final String productId;

    private final String expiresDate;

    private final String subscriptionStatus;

    private final boolean renewalStatus;

    @Builder
    private SubscribeResponse(String originalTransactionId, String transactionId, String productId, String expiresDate, String subscriptionStatus, boolean renewalStatus) {
        this.originalTransactionId = originalTransactionId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.expiresDate = expiresDate;
        this.subscriptionStatus = subscriptionStatus;
        this.renewalStatus = renewalStatus;
    }

    public static SubscribeResponse of(Subscribe subscribe){
        return SubscribeResponse.builder()
            .expiresDate(subscribe.getExpiresDate())
            .originalTransactionId(subscribe.getOriginalTransactionId())
            .productId(subscribe.getProductId())
            .renewalStatus(subscribe.isRenewalStatus())
            .transactionId(subscribe.getTransactionId())
            .subscriptionStatus(subscribe.getSubscriptionStatus())
            .build();

    }

}

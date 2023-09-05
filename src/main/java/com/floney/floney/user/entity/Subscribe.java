package com.floney.floney.user.entity;

import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.dto.request.SubscribeRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscribe extends BaseEntity {

    @OneToOne
    private User user;
    private String originalTransactionId;
    private String transactionId;
    private String productId;
    private String expiresDate;
    private String subscriptionStatus;
    private boolean renewalStatus;

    @Builder
    private Subscribe(User user, String originalTransactionId, String transactionId, String productId, String expiresDate, String subscriptionStatus, boolean renewalStatus) {
        this.user = user;
        this.originalTransactionId = originalTransactionId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.expiresDate = expiresDate;
        this.subscriptionStatus = subscriptionStatus;
        this.renewalStatus = renewalStatus;
    }

    public static Subscribe of(User user, SubscribeRequest request) {
        return Subscribe.builder()
            .expiresDate(request.getExpiresDate())
            .originalTransactionId(request.getOriginalTransactionId())
            .productId(request.getProductId())
            .renewalStatus(request.isRenewalStatus())
            .transactionId(request.getTransactionId())
            .user(user)
            .subscriptionStatus(request.getSubscriptionStatus())
            .build();
    }
}

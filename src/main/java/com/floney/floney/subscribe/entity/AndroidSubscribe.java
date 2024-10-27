package com.floney.floney.subscribe.entity;


import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.subscribe.dto.AndroidSubscriptionPurchase;
import com.floney.floney.user.entity.User;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import com.google.api.services.androidpublisher.model.SubscriptionPurchase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@RequiredArgsConstructor
public class AndroidSubscribe extends BaseEntity {

    @OneToOne
    private User user;

    private Integer paymentState;

    private String expiryTimeMillis;

    private Integer cancelReason;

    private String orderId;

    public AndroidSubscribe(final AndroidSubscriptionPurchase payload, final User user) {
        this.expiryTimeMillis = payload.getExpiryTimeMillis();
        this.paymentState = payload.getPaymentState();
        this.cancelReason = payload.getCancelReason();
        this.orderId = payload.getOrderId();
        this.user = user;
    }

}
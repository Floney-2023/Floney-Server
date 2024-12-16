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
import java.util.Map;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@RequiredArgsConstructor
public class AndroidSubscribe extends BaseEntity {

    @OneToOne
    private User user;

    private String paymentState;

    private String expiryTimeMillis;

    private Integer cancelReason;

    private String orderId;

    private String eventTimeMillis;

    public AndroidSubscribe(final Map<String, String> payload, final User user) {
        this.expiryTimeMillis = payload.get("expiryTimeMillis");
        this.paymentState = String.valueOf(payload.get("paymentState"));
        this.orderId = payload.get("orderId");
        this.user = user;
        this.eventTimeMillis = payload.get("eventTimeMillis");
    }

    public void update(User user, final Map<String, String> payload) {
        if (Long.parseLong(this.expiryTimeMillis) <= Long.parseLong(payload.get("expiryTimeMillis"))) {
            this.expiryTimeMillis = payload.get("expiryTimeMillis");
        }
        this.paymentState = String.valueOf(payload.get("paymentState"));
        this.orderId = payload.get("orderId");
        this.user = user;
        this.eventTimeMillis = payload.get("eventTimeMillis");
    }

}
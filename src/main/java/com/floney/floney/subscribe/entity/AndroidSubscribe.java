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

    private String startTimeMillis;

    private String autoResumeTimeMillis;

    private Boolean autoRenewing;

    private String priceCurrencyCode;

    private String priceAmountMicros;

    public AndroidSubscribe(final Map<String, Object> payload, final User user) {
        this.expiryTimeMillis = (String) payload.get("expiryTimeMillis");
        this.paymentState = String.valueOf(payload.get("paymentState"));
        this.orderId = (String) payload.get("orderId").toString().replaceAll("\\.+[0-9]+$", "");
        this.user = user;
        this.eventTimeMillis = (String) payload.get("eventTimeMillis");
        this.startTimeMillis = (String) payload.get("startTimeMillis");
        this.autoResumeTimeMillis = (String) payload.get("autoResumeTimeMillis");
        this.autoRenewing = (boolean) payload.get("autoRenewing");
        this.priceCurrencyCode = (String) payload.get("priceCurrencyCode");
        this.priceAmountMicros = (String) payload.get("priceAmountMicros");
    }

    public void update(User user, final Map<String, Object> payload) {
        if (Long.parseLong(this.expiryTimeMillis) <= Long.parseLong((String) payload.get("expiryTimeMillis"))) {
            this.expiryTimeMillis = (String) payload.get("expiryTimeMillis");
        }
        this.paymentState = String.valueOf(payload.get("paymentState"));
        this.orderId = (String) payload.get("orderId").toString().replaceAll("\\.+[0-9]+$", "");
        this.user = user;
        this.eventTimeMillis = (String) payload.get("eventTimeMillis");
        this.startTimeMillis = (String) payload.get("startTimeMillis");
        this.autoRenewing = (boolean) payload.get("autoRenewing");
        this.priceCurrencyCode = (String) payload.get("priceCurrencyCode");
        this.priceAmountMicros = (String) payload.get("priceAmountMicros");
    }

    public void update(final Map<String, Object> payload, User user) {
        if (Long.parseLong(this.expiryTimeMillis) <= Long.parseLong((String) payload.get("expiryTimeMillis"))) {
            this.expiryTimeMillis = (String) payload.get("expiryTimeMillis");
        }
        if (this.user == null) {
            this.user = user;
        }
        this.paymentState = String.valueOf(payload.get("paymentState"));
        this.orderId = (String) payload.get("orderId").toString().replaceAll("\\.+[0-9]+$", "");
        this.eventTimeMillis = (String) payload.get("eventTimeMillis");
        this.startTimeMillis = (String) payload.get("startTimeMillis");
        this.autoRenewing = (boolean) payload.get("autoRenewing");
        this.priceCurrencyCode = (String) payload.get("priceCurrencyCode");
        this.priceAmountMicros = (String) payload.get("priceAmountMicros");
    }

}
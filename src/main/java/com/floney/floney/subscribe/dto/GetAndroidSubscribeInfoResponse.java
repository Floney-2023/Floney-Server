package com.floney.floney.subscribe.dto;

import com.floney.floney.subscribe.entity.AndroidSubscribe;
import lombok.Getter;

@Getter
public class GetAndroidSubscribeInfoResponse {
    private Long id;
    private String paymentState;
    private String expiryTimeMillis;
    private Integer cancelReason;
    private String orderId;
    private String eventTimeMillis;
    private String startTimeMillis;
    private String autoResumeTimeMillis;
    private boolean autoRenewing;
    private String priceCurrencyCode;
    private String priceAmountMicros;
    private boolean active;

    public GetAndroidSubscribeInfoResponse(AndroidSubscribe response) {
        this.id = response.getId();
        this.paymentState = response.getPaymentState();
        this.expiryTimeMillis = response.getExpiryTimeMillis();
        this.cancelReason = response.getCancelReason();
        this.orderId = response.getOrderId();
        this.eventTimeMillis = response.getEventTimeMillis();
        this.startTimeMillis = response.getStartTimeMillis();
        this.autoResumeTimeMillis = response.getAutoResumeTimeMillis();
        this.autoRenewing = response.getAutoRenewing();
        this.priceCurrencyCode = response.getPriceCurrencyCode();
        this.priceAmountMicros = response.getPriceAmountMicros();
        this.active = response.isActive();
    }

}
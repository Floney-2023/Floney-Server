package com.floney.floney.subscribe.dto;

import lombok.Getter;

@Getter
public class GetSubscribeInfoResponse {
    private final boolean isActive;
    private final Long expiryTimeMillis;
    private final Long remainingDays;
    private final boolean autoRenewing;
    private final String priceCurrencyCode;
    private final String priceAmountMicros;
    private final boolean isCurrentSubscribe;
    
    // Android 구독 정보용 생성자
    public GetSubscribeInfoResponse(GetAndroidSubscribeInfoResponse androidResponse) {
        this.isActive = androidResponse.isActive() && androidResponse.isCurrentSubscribe();
        this.expiryTimeMillis = Long.parseLong(androidResponse.getExpiryTimeMillis());
        this.remainingDays = calculateRemainingDays(this.expiryTimeMillis);
        this.autoRenewing = androidResponse.isAutoRenewing();
        this.priceCurrencyCode = androidResponse.getPriceCurrencyCode();
        this.priceAmountMicros = androidResponse.getPriceAmountMicros();
        this.isCurrentSubscribe = this.remainingDays > 0;
    }
    
    // iOS 구독 정보용 생성자 (AppleSubscribe 엔티티 사용)
    public GetSubscribeInfoResponse(boolean isActive, Long expiryTimeMillis, String priceCurrencyCode, String priceAmountMicros) {
        this.isActive = isActive;
        this.expiryTimeMillis = expiryTimeMillis;
        this.remainingDays = calculateRemainingDays(expiryTimeMillis);
        this.autoRenewing = false; //TODO
        this.priceCurrencyCode = priceCurrencyCode;
        this.priceAmountMicros = priceAmountMicros;
        this.isCurrentSubscribe = this.remainingDays > 0;
    }

    
    private Long calculateRemainingDays(Long expiryTimeMillis) {
        if (expiryTimeMillis == null) return 0L;
        
        long currentTimeMillis = System.currentTimeMillis();
        long remainingMillis = expiryTimeMillis - currentTimeMillis;
        
        if (remainingMillis <= 0) return 0L;
        
        return remainingMillis / (24 * 60 * 60 * 1000); // 밀리초를 일수로 변환
    }
}
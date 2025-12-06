package com.floney.floney.subscribe.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class VoidedPurchaseNotification {
    String purchaseToken;
    String orderId;
    int productType;
    int refundType;
}

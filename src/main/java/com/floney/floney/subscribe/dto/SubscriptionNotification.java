package com.floney.floney.subscribe.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class SubscriptionNotification {
    int notificationType;
    String purchaseToken;
    String subscriptionId;
    String version;
}

package com.floney.floney.subscribe.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class GoogleRtndDto {
    String eventTimeMillis;

    String packageName;

    SubscriptionNotification subscriptionNotification;

    TestNotification testNotification;

    VoidedPurchaseNotification voidedPurchaseNotification;

    String version;
}


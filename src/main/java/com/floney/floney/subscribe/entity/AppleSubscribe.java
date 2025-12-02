package com.floney.floney.subscribe.entity;


import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@RequiredArgsConstructor
public class AppleSubscribe extends BaseEntity {

    @OneToOne
    private User user;

    private String transactionId;

    private Long expiresDate;

    private Long purchaseDate;

    private Long signedDate;

    private String currency;

    @Unique
    private String originalTransactionId;

    private Integer revocationReason;

    private String transactionReason;

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    public AppleSubscribe(final JWSTransactionDecodedPayload payload, final User user) {
        this.expiresDate = payload.getExpiresDate();
        this.currency = payload.getCurrency();
        this.transactionId = payload.getOriginalTransactionId();
        this.originalTransactionId = payload.getOriginalTransactionId();
        this.transactionReason = payload.getRawTransactionReason();
        this.revocationReason = payload.getRawRevocationReason();
        this.purchaseDate = payload.getPurchaseDate();
        this.signedDate = payload.getSignedDate();
        this.user = user;
        this.rawPayload = convertToJson(payload);
    }

    public void update(final JWSTransactionDecodedPayload payload) {
        if (this.signedDate >= payload.getSignedDate()) return;
        if(this.expiresDate >= payload.getExpiresDate()) return;

        this.expiresDate = payload.getExpiresDate();
        this.transactionReason = String.valueOf(payload.getTransactionReason());
        this.transactionId = payload.getOriginalTransactionId();
        this.signedDate = payload.getSignedDate();
        this.rawPayload = convertToJson(payload);
    }

    private String convertToJson(JWSTransactionDecodedPayload payload) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
        } catch (Exception e) {
            return null;
        }
    }

}
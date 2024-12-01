package com.floney.floney.subscribe.entity;


import com.apple.itunes.storekit.model.JWSTransactionDecodedPayload;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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

    public AppleSubscribe(final JWSTransactionDecodedPayload payload, final User user) {
        this.expiresDate = payload.getExpiresDate();
        this.currency = payload.getCurrency();
        this.transactionId = payload.getTransactionId();
        this.originalTransactionId = payload.getOriginalTransactionId();
        this.transactionReason = payload.getRawTransactionReason();
        this.revocationReason = payload.getRawRevocationReason();
        this.purchaseDate = payload.getPurchaseDate();
        this.signedDate = payload.getSignedDate();
        this.user = user;
    }

    public void update(final JWSTransactionDecodedPayload payload){
        this.expiresDate = payload.getPurchaseDate();
        this.transactionReason = String.valueOf(payload.getTransactionReason());
        this.transactionId = payload.getTransactionId();
        this.signedDate = payload.getSignedDate();
    }

}
package com.floney.floney.user.entity;

import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignoutOtherReason extends BaseEntity {

    @Column(nullable = false)
    private String reason;

    public static SignoutOtherReason from(final String reason) {
        return SignoutOtherReason.builder()
                .reason(reason)
                .build();
    }
}

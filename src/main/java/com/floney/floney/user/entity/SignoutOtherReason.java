package com.floney.floney.user.entity;

import com.floney.floney.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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

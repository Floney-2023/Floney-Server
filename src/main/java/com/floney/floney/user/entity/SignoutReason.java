package com.floney.floney.user.entity;

import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.user.dto.constant.SignoutType;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignoutReason extends BaseEntity {

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, unique = true, updatable = false)
    private SignoutType reasonType;

    @Column(nullable = false)
    private Long count;
}

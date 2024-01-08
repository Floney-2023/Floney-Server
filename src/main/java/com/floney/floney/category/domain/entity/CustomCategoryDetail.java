package com.floney.floney.category.domain.entity;

import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomCategoryDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private String name;
}

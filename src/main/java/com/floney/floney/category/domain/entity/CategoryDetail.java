package com.floney.floney.category.domain.entity;

import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CategoryDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @Column(nullable = false)
    private String name;
}

package com.floney.floney.book.domain.category.entity;

import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultSubcategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category parent;
    
    @Column(nullable = false)
    private String name;
}

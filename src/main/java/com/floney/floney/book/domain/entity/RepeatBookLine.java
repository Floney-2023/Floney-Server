package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatBookLine extends BaseEntity {

    @ManyToOne
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category lineCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subcategory lineSubcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subcategory assetSubcategory;

    @Column(nullable = false)
    private RepeatDuration repeatDuration;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double money;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean exceptStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookUser writer;


}

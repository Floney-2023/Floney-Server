package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.category.Category;
import com.floney.floney.book.domain.category.CustomSubCategory;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineCategory extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private BookLine bookLine;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category lineCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private CustomSubCategory lineSubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private CustomSubCategory assetSubCategory;
}

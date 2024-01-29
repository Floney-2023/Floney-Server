package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.CustomSubCategory;
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

    public static BookLineCategory create(final Category lineCategory,
                                          final CustomSubCategory lineSubCategory,
                                          final CustomSubCategory assetSubCategory) {
        return BookLineCategory.builder()
            .lineCategory(lineCategory)
            .lineSubCategory(lineSubCategory)
            .assetSubCategory(assetSubCategory)
            .build();
    }

    public void updateLineSubCategory(final CustomSubCategory lineSubCategory) {
        this.lineSubCategory = lineSubCategory;
    }

    public void updateAssetSubCategory(final CustomSubCategory assetSubCategory) {
        this.assetSubCategory = assetSubCategory;
    }

    public boolean isIncomeOrOutcome() {
        return lineCategory.isIncomeOrOutcome();
    }

    public boolean isIncome() {
        return lineCategory.isIncome();
    }

    public boolean isOutcome() {
        return lineCategory.isOutcome();
    }
}

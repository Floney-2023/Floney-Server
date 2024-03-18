package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
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
    private Subcategory lineSubcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subcategory assetSubcategory;

    public static BookLineCategory create(final Category lineCategory,
                                          final Subcategory lineSubcategory,
                                          final Subcategory assetSubcategory) {
        return BookLineCategory.builder()
            .lineCategory(lineCategory)
            .lineSubcategory(lineSubcategory)
            .assetSubcategory(assetSubcategory)
            .build();
    }

    public void setBookLine(final BookLine bookLine) {
        this.bookLine = bookLine;
    }

    public void updateLineSubCategory(final Subcategory lineSubcategory) {
        this.lineSubcategory = lineSubcategory;
    }

    public void updateAssetSubCategory(final Subcategory assetSubcategory) {
        this.assetSubcategory = assetSubcategory;
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

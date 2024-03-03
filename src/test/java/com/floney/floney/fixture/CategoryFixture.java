package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;

public class CategoryFixture {

    public static Category incomeCategory() {
        return Category.builder()
            .name(CategoryType.INCOME).build();
    }

    public static Category assetCategory() {
        return Category.builder()
            .name(CategoryType.ASSET).build();
    }
}

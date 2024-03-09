package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;

public class CategoryFixture {

    public static Category create(final CategoryType categoryType) {
        return Category.builder()
            .name(categoryType)
            .build();
    }
}

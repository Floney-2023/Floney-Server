package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;

public class CategoryFixture {

    public static Subcategory createSubcategory(Book book, Category parent, String name) {
        return Subcategory.builder()
            .book(book)
            .parent(parent)
            .name(name)
            .build();

    }

    public static Category incomeCategory() {
        return Category.builder()
            .name(CategoryType.INCOME).build();
    }

    public static Category assetCategory() {
        return Category.builder()
            .name(CategoryType.ASSET).build();
    }
}

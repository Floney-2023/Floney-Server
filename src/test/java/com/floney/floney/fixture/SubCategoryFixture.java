package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;

public class SubCategoryFixture {

    public static Subcategory createSubCategory(Book book, Category parent, String name) {
        return Subcategory.builder()
            .book(book)
            .parent(parent)
            .name(name)
            .build();

    }
}

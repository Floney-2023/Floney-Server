package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import org.springframework.test.util.ReflectionTestUtils;

public class SubcategoryFixture {

    public static Subcategory createSubcategory(final Book book,
                                                final Category parent,
                                                final String name) {
        return Subcategory.builder()
            .book(book)
            .parent(parent)
            .name(name)
            .build();
    }

    public static Subcategory createSubcategoryWithId(Book book, Category parent, String name, Long id) {
        Subcategory subCategory = Subcategory.builder()
            .book(book)
            .parent(parent)
            .name(name)
            .build();

        ReflectionTestUtils.setField(subCategory, "id", id);
        return subCategory;
    }

}

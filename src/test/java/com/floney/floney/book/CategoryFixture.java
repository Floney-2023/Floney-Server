package com.floney.floney.book;

import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class CategoryFixture {

    public static final String ROOT = "루트 카테고리";

    public static CreateCategoryRequest createRootRequest() {
        return CreateCategoryRequest.builder()
            .bookKey(BOOK_KEY.toString())
            .name(ROOT)
            .build();
    }

    public static Category createRootCategory(Book savedBook) {
        return Category.builder()
            .parent(null)
            .name(ROOT)
            .book(savedBook)
            .build();
    }

}

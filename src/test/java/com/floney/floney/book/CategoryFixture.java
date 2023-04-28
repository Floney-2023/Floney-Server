package com.floney.floney.book;

import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class CategoryFixture {

    public static final String ROOT = "부모 카테고리";

    public static CreateCategoryRequest createRootRequest() {
        return CreateCategoryRequest.builder()
            .bookKey(BOOK_KEY)
            .name(ROOT)
            .build();
    }

    public static Category createRootCategory() {
        return new Category(null,null,null,ROOT,null);
    }

}

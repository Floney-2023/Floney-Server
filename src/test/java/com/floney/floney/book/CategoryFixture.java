package com.floney.floney.book;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.DefaultCategory;

import static com.floney.floney.book.BookFixture.BOOK_KEY;

public class CategoryFixture {

    public static final String ROOT = "부모 카테고리";
    public static final String CHILD = "추가하는 카테고리";

    public static CreateCategoryRequest createRootRequest() {
        return CreateCategoryRequest.builder()
            .bookKey(BOOK_KEY)
            .name(ROOT)
            .build();
    }

    public static DefaultCategory createDefaultRoot(String name) {
        return DefaultCategory.builder()
            .name(name)
            .build();
    }

    public static DefaultCategory createDefaultChild(Category parent,String name) {
        return DefaultCategory.builder()
            .parent(parent)
            .name(name)
            .build();
    }

    public static BookCategory createRootCategory(Book book) {
        return BookCategory.builder()
            .book(book)
            .name(ROOT)
            .build();
    }

    public static BookCategory createChildCategory(Category parent,Book book) {
        return BookCategory.builder()
            .book(book)
            .parent(parent)
            .name(CHILD)
            .build();
    }


    public static CreateCategoryRequest createBookCategory(){
        return CreateCategoryRequest.builder()
            .bookKey(BOOK_KEY)
            .parent(ROOT)
            .name(CHILD)
            .build();
    }

    public static CategoryResponse categoryRootResponse(){
        return CategoryResponse.builder()
            .name(ROOT)
            .build();
    }

    public static CategoryResponse categoryChildResponse(){
        return CategoryResponse.builder()
            .name(CHILD)
            .build();
    }

}

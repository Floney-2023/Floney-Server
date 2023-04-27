package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class CreateCategoryRequest {

    private String bookKey;
    private String parent;
    private String name;

    @Builder
    public CreateCategoryRequest(String bookKey, String parent, String name) {
        this.bookKey = bookKey;
        this.parent = parent;
        this.name = name;
    }

    public Category of(Book book, Category savedParent) {
        Category parent = !Objects.equals(savedParent, Category.rootParent()) ? savedParent : null;
        return Category.builder()
            .book(book)
            .parent(parent)
            .name(name)
            .build();
    }

    public Boolean hasParent() {
        return parent != null;
    }
}

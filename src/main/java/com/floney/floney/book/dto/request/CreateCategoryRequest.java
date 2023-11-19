package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.domain.entity.DefaultCategory;
import com.floney.floney.book.domain.entity.category.BookCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
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

    public BookCategory of(Category savedParent, Book book) {
        Category parent = !Objects.equals(savedParent, DefaultCategory.rootParent()) ? savedParent : null;
        return BookCategory.builder()
                .book(book)
                .parent(parent)
                .name(name)
                .build();
    }

    public Boolean hasParent() {
        return parent != null;
    }
}

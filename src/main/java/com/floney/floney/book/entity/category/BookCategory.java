package com.floney.floney.book.entity.category;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SuperBuilder
public class BookCategory extends Category {

    @ManyToOne
    private Book book;

    private BookCategory(String name, Category parent, Book book) {
        super(name, parent);
        this.book = book;
    }

    public BookCategory delete() {
        this.book = null;
        return this;
    }
}

package com.floney.floney.book.entity;

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

    public BookCategory(String name, Category parent, Book book, Boolean status) {
        super(name,parent,status);
        this.book = book;
    }
}

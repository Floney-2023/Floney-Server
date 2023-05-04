package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface BookUserCustomRepository {

    void isMax(Book book);

    BookUser findUserWith(String auth, String bookKey);

    BooleanExpression existBookUser(String email, String code);
}

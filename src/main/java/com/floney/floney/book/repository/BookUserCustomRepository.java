package com.floney.floney.book.repository;

import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

public interface BookUserCustomRepository {

    void isMax(Book book);

    BookUser findUserWith(String auth, String bookKey);

    BooleanExpression existBookUser(String email, String code);

    List<MyBookInfo> findMyBooks(User user);

    void countBookUser(Book book);
}

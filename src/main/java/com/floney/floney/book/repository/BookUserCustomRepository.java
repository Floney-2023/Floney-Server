package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;

public interface BookUserCustomRepository {

    void isMax(Book book);

    BookUser findUserWith(String auth, String bookKey);
}

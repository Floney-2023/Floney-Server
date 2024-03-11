package com.floney.floney.book.repository;

import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;

import java.util.List;

public interface RepeatBookLineCustomRepository {

    void inactiveAllByBook(Book book);

    void inactiveAllByBookUser(BookUser targetBookUser);

    void inactiveAllBySubcategory(Subcategory subcategory);

    List<RepeatBookLine> findAllBySubcategory(Subcategory subcategory);
}

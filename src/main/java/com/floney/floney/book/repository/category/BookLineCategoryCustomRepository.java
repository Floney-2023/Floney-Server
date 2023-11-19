package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;

public interface BookLineCategoryCustomRepository {

    void inactiveAllByBookKey(String bookKey);

    void inactiveAllByBookLineId(Long bookLineId);

    void inactiveAllByBookUser(BookUser bookUser);

    void inactiveAllByBook(Book book);
}

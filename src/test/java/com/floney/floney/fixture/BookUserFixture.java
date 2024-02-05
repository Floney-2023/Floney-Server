package com.floney.floney.fixture;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.user.entity.User;

public class BookUserFixture {
    public static BookUser createBookUser(Book book, User user) {
        return BookUser.builder()
            .book(book)
            .user(user)
            .build();
    }
}

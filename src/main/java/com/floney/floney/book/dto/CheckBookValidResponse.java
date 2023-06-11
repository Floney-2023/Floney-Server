package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import lombok.Getter;

@Getter
public class CheckBookValidResponse {
    private String bookKey;

    private CheckBookValidResponse() {
    }

    private CheckBookValidResponse(String bookKey) {
        this.bookKey = bookKey;
    }

    public static CheckBookValidResponse userBook(Book book) {
        if (book.equals(Book.initBook())) {
            return new CheckBookValidResponse();
        }
        return new CheckBookValidResponse(book.getBookKey());
    }
}

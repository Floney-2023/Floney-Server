package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckBookValidResponse {
    private String bookKey;

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

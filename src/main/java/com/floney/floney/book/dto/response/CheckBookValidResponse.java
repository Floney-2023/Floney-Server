package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

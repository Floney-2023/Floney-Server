package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookStatusResponse {
    private Status bookStatus;

    public static BookStatusResponse of(Book book) {
        return new BookStatusResponse(book.getBookStatus());
    }
}

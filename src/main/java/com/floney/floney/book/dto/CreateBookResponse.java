package com.floney.floney.book.dto;


import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateBookResponse {
    private String bookKey;
    private String code;

    @Builder
    private CreateBookResponse(String bookKey, String code) {
        this.bookKey = bookKey;
        this.code = code;
    }

    public static CreateBookResponse of(Book book) {
        return CreateBookResponse.builder()
            .bookKey(book.getBookKey())
            .code(book.getCode())
            .build();
    }
}

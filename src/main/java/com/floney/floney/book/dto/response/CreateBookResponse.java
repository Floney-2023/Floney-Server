package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateBookResponse {
    private String bookKey;

    private String code;

    @Builder
    public CreateBookResponse(String bookKey, String code) {
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

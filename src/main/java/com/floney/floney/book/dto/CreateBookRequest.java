package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.util.CodeFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateBookRequest {
    private String name;
    private String bookImg;

    @Builder
    private CreateBookRequest(String name, String bookImg) {
        this.name = name;
        this.bookImg = bookImg;
    }

    public Book of(String email) {
        return Book.builder()
            .bookKey(CodeFactory.generateCode())
            .name(name)
            .bookImg(bookImg)
            .owner(email)
            .code(CodeFactory.generateCode())
            .build();

    }


}

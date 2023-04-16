package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.service.CodeFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CreateBookRequest {
    private String name;
    private String profileImg;

    @Builder
    private CreateBookRequest(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
    }

    public Book of(String email) {
        return Book.builder()
            .bookKey(CodeFactory.bookKey())
            .name(name)
            .profileImg(profileImg)
            .provider(email)
            .code(CodeFactory.generateCode())
            .build();

    }

}

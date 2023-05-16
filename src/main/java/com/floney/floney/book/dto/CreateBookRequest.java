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
    private String profileImg;

    @Builder
    private CreateBookRequest(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
    }

    public Book of(String email) {
        return Book.builder()
            .bookKey(CodeFactory.generateCode())
            .name(name)
            .profileImg(profileImg)
            .providerEmail(email)
            .code(CodeFactory.generateCode())
            .build();

    }


}

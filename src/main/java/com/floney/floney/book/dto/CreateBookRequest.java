package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
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

    public Book of(String code, String email) {
        return Book.builder()
            .name(name)
            .profileImg(profileImg)
            .provider(email)
            .code(code)
            .build();
    }

}

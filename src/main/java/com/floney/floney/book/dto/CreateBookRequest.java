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
    private String email;
    @Builder
    private CreateBookRequest(String name, String profileImg, String email) {
        this.name = name;
        this.profileImg = profileImg;
        this.email = email;
    }
    public Book of(String code) {
        return Book.builder()
            .name(name)
            .profileImg(profileImg)
            .provider(email)
            .code(code)
            .build();
    }

}

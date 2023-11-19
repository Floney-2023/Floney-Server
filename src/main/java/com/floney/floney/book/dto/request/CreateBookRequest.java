package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.BookUserCapacity;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.util.CodeFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateBookRequest {
    private String name;
    private String profileImg;

    @Builder
    private CreateBookRequest(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
    }

    public Book to(String email) {
        return Book.builder()
                .bookKey(CodeFactory.generateCode())
                .name(name)
                .profileImg(profileImg)
                .owner(email)
                .code(CodeFactory.generateCode())
                .userCapacity(BookUserCapacity.DEFAULT.getValue())
                .build();
    }
}

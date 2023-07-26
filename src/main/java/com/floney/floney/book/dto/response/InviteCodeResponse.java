package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import lombok.Getter;

@Getter
public class InviteCodeResponse {
    private String code;

    public InviteCodeResponse(Book book) {
        this.code = book.getCode();
    }

}

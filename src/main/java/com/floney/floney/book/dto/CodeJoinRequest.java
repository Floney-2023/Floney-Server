package com.floney.floney.book.dto;

import lombok.Getter;

@Getter
public class CodeJoinRequest {
    private String code;

    public CodeJoinRequest(String code) {
        this.code = code;
    }
}

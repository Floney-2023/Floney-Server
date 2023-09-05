package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CodeJoinRequest {
    private String code;

    public CodeJoinRequest(String code) {
        this.code = code;
    }
}

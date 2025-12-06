package com.floney.floney.subscribe.dto;

import lombok.Getter;

@Getter
public class IsSubscribeUserResponse {
    public boolean maxBook;

    public IsSubscribeUserResponse(boolean maxBook) {
        this.maxBook = maxBook;
    }

}

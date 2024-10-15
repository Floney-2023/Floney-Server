package com.floney.floney.subscribe.dto;

import lombok.Getter;

@Getter
public class GetAppleTransactionResponse {
    public boolean isValid;

    public GetAppleTransactionResponse(boolean isValid) {
        this.isValid = isValid;
    }

}

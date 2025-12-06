package com.floney.floney.subscribe.dto;

import lombok.Getter;

@Getter
public class GetTransactionResponse {
    public boolean isValid;

    public GetTransactionResponse(boolean isValid) {
        this.isValid = isValid;
    }

}

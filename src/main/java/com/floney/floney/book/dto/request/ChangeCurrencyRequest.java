package com.floney.floney.book.dto.request;

import com.floney.floney.book.dto.constant.Currency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangeCurrencyRequest {
    private Currency requestCurrency;
    private String bookKey;

    public ChangeCurrencyRequest(Currency currency, String bookKey) {
        this.requestCurrency = currency;
        this.bookKey = bookKey;
    }
}

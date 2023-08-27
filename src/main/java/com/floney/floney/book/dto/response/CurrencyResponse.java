package com.floney.floney.book.dto.response;

import com.floney.floney.book.dto.constant.Currency;
import com.floney.floney.book.entity.Book;
import lombok.Getter;

@Getter
public class CurrencyResponse {
    private final String myBookCurrency;

    private CurrencyResponse(String myBookCurrency) {
        this.myBookCurrency = myBookCurrency;
    }

    public static CurrencyResponse of(Book book){
        return new CurrencyResponse(book.getCurrency());
    }
}

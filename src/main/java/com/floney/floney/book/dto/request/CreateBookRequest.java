package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.BookUserCapacity;
import com.floney.floney.book.domain.Currency;
import com.floney.floney.book.domain.entity.Book;
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
    private Currency requestCurrency; // Currency type based on user's locale (optional, defaults to USD)

    @Builder
    private CreateBookRequest(String name, String profileImg, Currency requestCurrency) {
        this.name = name;
        this.profileImg = profileImg;
        this.requestCurrency = requestCurrency;
    }

    public Book to(String email) {
        return Book.builder()
            .bookKey(CodeFactory.generateCode())
            .name(name)
            .profileImg(profileImg)
            .owner(email)
            .seeProfile(true)
            .code(CodeFactory.generateCode())
            .userCapacity(BookUserCapacity.DEFAULT.getValue())
            .currency(getCurrency())
            .build();
    }

    private String getCurrency() {
        // Use requestCurrency if provided, otherwise default to USD
        return requestCurrency != null ? requestCurrency.name() : Currency.USD.name();
    }
}

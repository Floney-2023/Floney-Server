package com.floney.floney.book.domain.category;

import lombok.Getter;

@Getter
public enum CategoryType {

    INCOME("수입"),
    OUTCOME("지출"),
    TRANSFER("이체"),
    ASSET("자산");

    private final String meaning;

    CategoryType(final String meaning) {
        this.meaning = meaning;
    }
}

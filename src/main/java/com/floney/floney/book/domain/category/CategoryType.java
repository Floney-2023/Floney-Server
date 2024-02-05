package com.floney.floney.book.domain.category;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CategoryType {

    /* Line Type */
    INCOME("수입"),
    OUTCOME("지출"),
    TRANSFER("이체"),

    /* Others */
    ASSET("자산");

    private final String meaning;

    CategoryType(final String meaning) {
        this.meaning = meaning;
    }

    public static CategoryType findByMeaning(final String meaning) {
        return Arrays.stream(CategoryType.values())
            .filter(categoryType -> categoryType.meaning.equals(meaning))
            .findFirst()
            .orElseThrow();
    }

    public static CategoryType findLineByMeaning(final String meaning) {
        return Arrays.stream(CategoryType.values())
            .filter(categoryType -> categoryType.meaning.equals(meaning))
            .filter(categoryType -> !CategoryType.ASSET.equals(categoryType))
            .findFirst()
            .orElseThrow();
    }

    public static boolean isLine(final CategoryType categoryType) {
        return INCOME.equals(categoryType) || OUTCOME.equals(categoryType) || TRANSFER.equals(categoryType);
    }
}

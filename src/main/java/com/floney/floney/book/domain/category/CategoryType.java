package com.floney.floney.book.domain.category;

import com.floney.floney.common.exception.book.NotFoundCategoryException;
import com.floney.floney.common.exception.book.NotFoundParentCategoryException;
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
            .orElseThrow(() -> new NotFoundParentCategoryException(meaning));
    }

    public static CategoryType findLineByMeaning(final String meaning) {
        return Arrays.stream(CategoryType.values())
            .filter(categoryType -> categoryType.meaning.equals(meaning))
            .filter(categoryType -> !CategoryType.ASSET.equals(categoryType))
            .findFirst()
            .orElseThrow(() -> new NotFoundParentCategoryException(meaning));
    }

    public void validateLineType() {
        if (INCOME.equals(this) || OUTCOME.equals(this) || TRANSFER.equals(this)) {
            return;
        }
        throw new NotFoundCategoryException(this.meaning);
    }
}

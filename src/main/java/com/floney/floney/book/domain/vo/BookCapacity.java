package com.floney.floney.book.domain.vo;

import lombok.Getter;

@Getter
public enum BookCapacity {

    DEFAULT(2);

    private final int value;

    BookCapacity(int value) {
        this.value = value;
    }
}

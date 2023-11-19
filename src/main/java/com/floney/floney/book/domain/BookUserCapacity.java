package com.floney.floney.book.domain;

import lombok.Getter;

@Getter
public enum BookUserCapacity {

    DEFAULT(4);

    private final int value;

    BookUserCapacity(final int value) {
        this.value = value;
    }
}

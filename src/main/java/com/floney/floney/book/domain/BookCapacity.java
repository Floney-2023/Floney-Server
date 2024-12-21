package com.floney.floney.book.domain;

import lombok.Getter;

@Getter
public enum BookCapacity {

    DEFAULT(2),
    SUBSCRIBE(4);

    private final int value;

    BookCapacity(int value) {
        this.value = value;
    }
}

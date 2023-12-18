package com.floney.floney.book.dto.constant;

import lombok.Getter;

@Getter
public enum CategoryEnum {

    FLOW("내역"),
    ASSET("자산"),
    FLOW_LINE("내역분류");

    private final String name;

    CategoryEnum(final String name) {
        this.name = name;
    }
}

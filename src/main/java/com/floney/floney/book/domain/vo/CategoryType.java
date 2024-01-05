package com.floney.floney.book.domain.vo;

import lombok.Getter;

@Getter
public enum CategoryType {

    FLOW("내역"),
    ASSET("자산"),
    FLOW_LINE("내역분류");

    private final String name;

    CategoryType(final String name) {
        this.name = name;
    }
}

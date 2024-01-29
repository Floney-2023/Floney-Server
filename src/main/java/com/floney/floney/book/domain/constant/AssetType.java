package com.floney.floney.book.domain.constant;

import lombok.Getter;

@Getter
public enum AssetType {

    OUTCOME("지출"),
    INCOME("수입"),
    BANK("이체");

    private final String kind;

    AssetType(String kind) {
        this.kind = kind;
    }
}

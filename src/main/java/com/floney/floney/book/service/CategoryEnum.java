package com.floney.floney.book.service;

import lombok.Getter;

@Getter
public enum CategoryEnum {
    FLOW("내역"),
    ASSET("자산"),
    FLOW_LINE("내역분류");
    private String rootName;

    CategoryEnum(String rootName) {
        this.rootName = rootName;
    }



}

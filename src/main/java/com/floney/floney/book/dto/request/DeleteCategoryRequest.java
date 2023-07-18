package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteCategoryRequest {
    private String bookKey;
    private String root;
    private String name;

    public DeleteCategoryRequest(String bookKey, String root, String name) {
        this.bookKey = bookKey;
        this.root = root;
        this.name = name;
    }
}

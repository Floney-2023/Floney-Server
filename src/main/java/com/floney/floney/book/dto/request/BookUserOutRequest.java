package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class BookUserOutRequest {
    private String bookKey;
}

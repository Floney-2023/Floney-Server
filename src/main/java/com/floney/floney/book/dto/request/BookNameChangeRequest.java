package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookNameChangeRequest {
    private String name;
    private String bookKey;
}

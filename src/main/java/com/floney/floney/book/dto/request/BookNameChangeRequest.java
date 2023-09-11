package com.floney.floney.book.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class BookNameChangeRequest {
    private String name;
    private String bookKey;
}

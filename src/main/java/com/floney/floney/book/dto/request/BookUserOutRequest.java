package com.floney.floney.book.dto.request;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BookUserOutRequest {
    public String bookKey;
}

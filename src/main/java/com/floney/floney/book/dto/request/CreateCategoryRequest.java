package com.floney.floney.book.dto.request;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCategoryRequest {

    // TODO: validation 추가
    private String bookKey;
    private String parent;
    private String name;
}

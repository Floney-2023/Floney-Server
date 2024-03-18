package com.floney.floney.book.dto.request;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteCategoryRequest {

    // TODO: validation 추가
    private String parent;
    private String name;
}

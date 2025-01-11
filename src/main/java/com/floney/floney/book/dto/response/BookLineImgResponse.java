package com.floney.floney.book.dto.response;

import com.floney.floney.book.domain.entity.BookLineImg;
import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookLineImgResponse {

    long id;

    String url;

    public BookLineImgResponse(BookLineImg bookLineImg){
        this.id = bookLineImg.getId();
        this.url = bookLineImg.getImgUrl();
    }
}

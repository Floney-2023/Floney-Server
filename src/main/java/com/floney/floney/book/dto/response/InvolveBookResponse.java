package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvolveBookResponse {
    private String bookKey;
    private Status bookStatus;

    private InvolveBookResponse(String bookKey, Status bookStatus) {
        this.bookKey = bookKey;
        this.bookStatus = bookStatus;
    }

    public static InvolveBookResponse of(Optional<Book> book) {
        // TODO: 파라미터의 Optional 제거
        return book.map(value -> new InvolveBookResponse(value.getBookKey(), value.getBookStatus()))
                .orElseGet(() -> new InvolveBookResponse(null, null));
    }
}

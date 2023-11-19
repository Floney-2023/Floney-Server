package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvolveBookResponse {

    private String bookKey;

    private InvolveBookResponse(String bookKey) {
        this.bookKey = bookKey;
    }

    public static InvolveBookResponse of(Optional<Book> book) {
        // TODO: 파라미터의 Optional 제거
        return book.map(value -> new InvolveBookResponse(value.getBookKey()))
                .orElseGet(() -> new InvolveBookResponse(null));
    }
}

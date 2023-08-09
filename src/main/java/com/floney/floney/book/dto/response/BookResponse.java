package com.floney.floney.book.dto.response;

import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookResponse {

    private String name;

    private String bookImg;

    private Boolean seeProfile;

    private Long initialAsset;

    private String bookKey;

    private Long budget;

    private Boolean carryOverStatus;

    private String code;

    @Builder
    private BookResponse(String name, String bookImg, Boolean seeProfile, Long initialAsset, String bookKey, Long budget, Boolean carryOverStatus, String code) {
        this.name = name;
        this.bookImg = bookImg;
        this.seeProfile = seeProfile;
        this.initialAsset = initialAsset;
        this.bookKey = bookKey;
        this.budget = budget;
        this.carryOverStatus = carryOverStatus;
        this.code = code;
    }

    public static BookResponse of(Book newBook) {
        return BookResponse.builder()
            .name(newBook.getName())
            .bookImg(newBook.getBookImg())
            .seeProfile(newBook.getSeeProfile())
            .initialAsset(newBook.getInitialAsset())
            .bookKey(newBook.getBookKey())
            .budget(newBook.getBudget())
            .carryOverStatus(newBook.getCarryOverStatus())
            .code(newBook.getCode())
            .build();
    }

    public static BookResponse init() {
        return new BookResponse();
    }
}

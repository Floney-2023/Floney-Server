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

    private Long initAsset;

    private String bookKey;

    private Long initBudget;

    private Boolean carryOverStatus;

    private String code;

    @Builder
    private BookResponse(String name, String bookImg, Boolean seeProfile, Long initAsset, String bookKey, Long initBudget, Boolean carryOverStatus, String code) {
        this.name = name;
        this.bookImg = bookImg;
        this.seeProfile = seeProfile;
        this.initAsset = initAsset;
        this.bookKey = bookKey;
        this.carryOverStatus = carryOverStatus;
        this.initBudget = initBudget;
        this.code = code;
    }

    public static BookResponse of(Book newBook) {
        return BookResponse.builder()
            .name(newBook.getName())
            .bookImg(newBook.getBookImg())
            .seeProfile(newBook.getSeeProfile())
            .initAsset(newBook.getInitAsset())
            .bookKey(newBook.getBookKey())
            .carryOverStatus(newBook.getCarryOverStatus())
            .code(newBook.getCode())
            .build();
    }

    public static BookResponse init() {
        return new BookResponse();
    }
}

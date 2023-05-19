package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookResponse {

    private String name;

    private String profileImg;

    private Boolean seeProfile;

    private Long initialAsset;

    private String bookKey;

    private Long budget;

    private Boolean carryOver;

    private String code;

    @Builder
    private BookResponse(String name, String profileImg, Boolean seeProfile, Long initialAsset, String bookKey, Long budget, Boolean carryOver, String code) {
        this.name = name;
        this.profileImg = profileImg;
        this.seeProfile = seeProfile;
        this.initialAsset = initialAsset;
        this.bookKey = bookKey;
        this.budget = budget;
        this.carryOver = carryOver;
        this.code = code;
    }

    public static BookResponse of(Book newBook) {
        return BookResponse.builder()
            .name(newBook.getName())
            .profileImg(newBook.getProfileImg())
            .seeProfile(newBook.getSeeProfile())
            .initialAsset(newBook.getInitialAsset())
            .bookKey(newBook.getBookKey())
            .budget(newBook.getBudget())
            .carryOver(newBook.getCarryOver())
            .code(newBook.getCode())
            .build();
    }

    public static BookResponse init() {
        return new BookResponse();
    }
}

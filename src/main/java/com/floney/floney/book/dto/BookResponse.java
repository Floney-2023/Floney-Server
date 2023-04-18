package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {

    private String name;

    private String profileImg;

    private Boolean seeProfile;

    private Long initialAsset;

    private String bookKey;

    private Long budget;

    private int weekStartDay;

    private Boolean carryOver;

    private String code;

    @Builder
    private BookResponse(String name, String profileImg, Boolean seeProfile, Long initialAsset, String bookKey, Long budget, int weekStartDay, Boolean carryOver, String code) {
        this.name = name;
        this.profileImg = profileImg;
        this.seeProfile = seeProfile;
        this.initialAsset = initialAsset;
        this.bookKey = bookKey;
        this.budget = budget;
        this.weekStartDay = weekStartDay;
        this.carryOver = carryOver;
        this.code = code;
    }

    public static BookResponse of(Book newBook){
        return BookResponse.builder()
            .name(newBook.getName())
            .profileImg(newBook.getProfileImg())
            .seeProfile(newBook.getSeeProfile())
            .initialAsset(newBook.getInitialAsset())
            .bookKey(newBook.getBookKey())
            .budget(newBook.getBudget())
            .weekStartDay(newBook.getWeekStartDay())
            .carryOver(newBook.getCarryOver())
            .code(newBook.getCode())
            .build();
    }

}

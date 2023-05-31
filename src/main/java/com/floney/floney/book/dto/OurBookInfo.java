package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

import static com.floney.floney.book.util.DateFactory.formatToDate;

public class OurBookInfo {
    private String bookImg;
    private String bookName;
    private LocalDate startDay;

    @Builder
    public OurBookInfo(String bookImg, String bookName, LocalDate startDay) {
        this.bookImg = bookImg;
        this.bookName = bookName;
        this.startDay = startDay;
    }

    public static OurBookInfo of(Book book, List<OurBookUser> bookUsers) {
        return OurBookInfo.builder()
            .bookImg(book.getProfileImg())
            .bookName(book.getName())
            .startDay(formatToDate(book.getCreatedAt()))
            .build();
    }

    private void isProviderOfBook(Book book,List<OurBookUser> bookUsers){
        for(OurBookUser bookUser : bookUsers) {
            bookUser.isProvider(book.getProviderEmail());
        }
    }
}

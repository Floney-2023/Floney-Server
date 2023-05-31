package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

import static com.floney.floney.book.util.DateFactory.formatToDate;

@Getter
public class OurBookInfo {
    private String bookImg;
    private String bookName;
    private LocalDate startDay;

    private List<OurBookUser> ourBookUsers;

    @Builder
    public OurBookInfo(String bookImg, String bookName, LocalDate startDay, List<OurBookUser> bookUsers) {
        this.bookImg = bookImg;
        this.bookName = bookName;
        this.startDay = startDay;
        this.ourBookUsers = bookUsers;
    }

    public static OurBookInfo of(Book book, List<OurBookUser> bookUsers, String myEmail) {
        checkRole(book, bookUsers);
        iSMyAccount(bookUsers, myEmail);

        return OurBookInfo.builder()
            .bookImg(book.getProfileImg())
            .bookName(book.getName())
            .startDay(formatToDate(book.getCreatedAt()))
            .bookUsers(bookUsers)
            .build();
    }

    private static void checkRole(Book book, List<OurBookUser> bookUsers) {
        for (OurBookUser bookUser : bookUsers) {
            bookUser.checkRole(book.getProviderEmail());
        }
    }

    private static void iSMyAccount(List<OurBookUser> bookUsers, String myEmail) {
        for (OurBookUser bookUser : bookUsers) {
            bookUser.isMyAccount(myEmail);
        }
    }
}

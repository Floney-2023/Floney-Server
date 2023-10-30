package com.floney.floney.book.dto.process;

import com.floney.floney.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

import static com.floney.floney.book.util.DateFactory.formatToDate;

@Getter
public class OurBookInfo {
    private final String bookImg;
    private final String bookName;
    private final LocalDate startDay;
    private final boolean seeProfileStatus;
    private final boolean carryOver;
    private final List<OurBookUser> ourBookUsers;

    @Builder
    public OurBookInfo(String bookImg, String bookName, LocalDate startDay, boolean seeProfileStatus, boolean carryOver, List<OurBookUser> bookUsers) {
        this.bookImg = bookImg;
        this.bookName = bookName;
        this.startDay = startDay;
        this.seeProfileStatus = seeProfileStatus;
        this.carryOver = carryOver;
        this.ourBookUsers = bookUsers;
    }


    public static OurBookInfo of(Book book, List<OurBookUser> bookUsers, String myEmail) {
        checkRole(book, bookUsers);
        iSMyAccount(bookUsers, myEmail);

        return OurBookInfo.builder()
                .bookImg(book.getBookImg())
                .bookName(book.getName())
                .startDay(formatToDate(book.getCreatedAt()))
                .bookUsers(bookUsers)
                .seeProfileStatus(book.getSeeProfile())
                .carryOver(book.getCarryOverStatus())
                .build();
    }

    private static void checkRole(Book book, List<OurBookUser> bookUsers) {
        bookUsers.forEach((bookUser) -> {
            bookUser.checkRole(book.getOwner());
        });
    }

    private static void iSMyAccount(List<OurBookUser> bookUsers, String myEmail) {
        bookUsers.forEach((bookUser) -> {
            bookUser.isMyAccount(myEmail);
        });
    }
}

package com.floney.floney.common.dto;

import com.floney.floney.book.entity.Book;
import com.floney.floney.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DelegateResponse {
    private final boolean isDelegate;
    private final String bookName;
    private final String delegateTo;
    private final String bookKey;

    @Builder
    private DelegateResponse(boolean isDelegate, String bookName, String delegateTo, String bookKey) {
        this.isDelegate = isDelegate;
        this.bookName = bookName;
        this.delegateTo = delegateTo;
        this.bookKey = bookKey;
    }

    public static DelegateResponse of(Book book, User user) {
        boolean delegate = false;
        String nickName = null;

        if (user != null) {
            delegate = true;
            nickName = user.getNickname();
        }

        return DelegateResponse.builder()
            .isDelegate(delegate)
            .bookName(book.getName())
            .bookKey(book.getBookKey())
            .delegateTo(nickName)
            .build();
    }
}

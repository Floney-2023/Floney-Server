package com.floney.floney.book.dto;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public class CheckBookValidResponse {
    private String bookKey;

    private CheckBookValidResponse(String bookKey) {
        this.bookKey = bookKey;
    }

    public static CheckBookValidResponse noneBook() {
        return new CheckBookValidResponse(null);
    }

    public static CheckBookValidResponse userBook(List<BookUser> bookUsers){
        bookUsers.sort(Comparator.comparing(BookUser::getUpdatedAt).reversed());
        Book recentBook = bookUsers.get(0)
            .getBook();
        return new CheckBookValidResponse(recentBook.getBookKey());
    }
}

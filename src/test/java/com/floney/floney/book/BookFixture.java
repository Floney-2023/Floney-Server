package com.floney.floney.book;

import com.floney.floney.book.dto.BookResponse;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.dto.CreateBookResponse;
import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;

public class BookFixture {

    public static String NAME = "플로니";
    public static String EMAIL = "floney@naver.com";
    public static String URL = "https://fileisHere.com";
    public static String CODE = "codeExample";
    public static String BOOK_KEY = "book-key";
    public static Long DEFAULT_VALUE = 0L;



    public static CreateBookRequest createBookRequest() {
        return CreateBookRequest.builder()
            .name(NAME)
            .profileImg(URL)
            .build();
    }

    public static Book createBookWith(String bookKey) {
        return Book.builder()
            .name(NAME)
            .profileImg(URL)
            .bookKey(bookKey)
            .budget(DEFAULT_VALUE)
            .provider(EMAIL)
            .code(CODE)
            .initialAsset(DEFAULT_VALUE)
            .build();
    }

    public static Book createBook() {
        return Book.builder()
            .name(NAME)
            .profileImg(URL)
            .bookKey(BOOK_KEY)
            .budget(DEFAULT_VALUE)
            .provider(EMAIL)
            .code(CODE)
            .initialAsset(DEFAULT_VALUE)
            .build();
    }

    public static CreateBookResponse bookResponse() {
        return CreateBookResponse.builder()
            .bookKey(BOOK_KEY)
            .code(CODE)
            .build();
    }

    public static MyBookInfo myBookInfo() {
        return MyBookInfo.builder()
            .name(NAME)
            .bookImg(URL)
            .memberCount(1L)
            .build();
    }

    public static BookUser createBookUser(User user, Book book){
        return BookUser.builder()
            .book(book)
            .user(user)
            .build();
    }

}

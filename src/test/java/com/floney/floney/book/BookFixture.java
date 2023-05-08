package com.floney.floney.book;

import com.floney.floney.book.dto.BookResponse;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.entity.Book;

import java.util.UUID;

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

    public static Book createBookWith(Long id,String bookKey) {
        return Book.builder()
            .id(id)
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
    public static BookResponse bookResponse() {
        return BookResponse.builder()
            .name(NAME)
            .profileImg(URL)
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

}

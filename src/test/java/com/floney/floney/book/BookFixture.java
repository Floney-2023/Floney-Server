package com.floney.floney.book;

import com.floney.floney.book.dto.BookResponse;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.entity.Book;

import java.util.UUID;

public class BookFixture {

    private static String NAME = "플로니";
    public static String EMAIL = "floney@naver.com";
    private static String URL = "https://fileisHere.com";
    public static String CODE = "codeExample";

    public static UUID BOOK_KEY = UUID.fromString("29d16907-61b6-4587-a584-72a0f344ff4e");

    private static Long DEFAULT_VALUE = 0L;


    public static CreateBookRequest createBookRequest() {
        return CreateBookRequest.builder()
            .name(NAME)
            .profileImg(URL)
            .build();
    }

    public static Book createBookWith(Long id) {
        return Book.builder()
            .id(id)
            .name(NAME)
            .profileImg(URL)
            .bookKey(BOOK_KEY)
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
    public static BookResponse bookResponse(){
        return BookResponse.builder()
            .name(NAME)
            .profileImg(URL)
            .code(CODE)
            .build();
    }

}

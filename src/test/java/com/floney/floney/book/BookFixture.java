package com.floney.floney.book;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.CodeJoinRequest;
import com.floney.floney.book.dto.request.CreateBookRequest;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;

public class BookFixture {

    public static String NAME = "플로니";
    public static String EMAIL = "floney@naver.com";
    public static String URL = "https://fileisHere.com";
    public static String UPDATE_URL = "https://fileisUpdate.com";
    public static String CODE = "code";
    public static String BOOK_KEY = "book-key";
    public static Long DEFAULT_VALUE = 0L;


    public static CreateBookRequest createBookRequest() {
        return CreateBookRequest.builder()
            .name(NAME)
            .bookImg(URL)
            .build();
    }

    public static Book createBookWith(String bookKey) {
        return Book.builder()
            .name(NAME)
            .bookImg(URL)
            .bookKey(bookKey)
            .budget(DEFAULT_VALUE)
            .owner(EMAIL)
            .code(CODE)
            .initialAsset(DEFAULT_VALUE)
            .build();
    }

    public static CodeJoinRequest codeJoinRequest() {
        return new CodeJoinRequest(CODE);
    }

    public static Book createBook() {
        return Book.builder()
            .name(NAME)
            .bookImg(URL)
            .bookKey(BOOK_KEY)
            .budget(DEFAULT_VALUE)
            .owner(EMAIL)
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

    public static BookUser createBookUser(User user, Book book) {
        return BookUser.builder()
            .book(book)
            .user(user)
            .build();
    }

    public static OurBookUser createOurBookUser() {
        return OurBookUser.builder()
            .name(NAME)
            .profileImg(URL)
            .email(EMAIL)
            .build();
    }

    public static UpdateBookImgRequest updateBookImgRequest(){
        return UpdateBookImgRequest.builder()
            .bookKey(BOOK_KEY)
            .newUrl(UPDATE_URL)
            .build();
    }

}

package com.floney.floney.fixture;

import com.floney.floney.book.domain.BookUserCapacity;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.CodeJoinRequest;
import com.floney.floney.book.dto.request.CreateBookRequest;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.util.CodeFactory;

import static com.floney.floney.fixture.UserFixture.DEFAULT_EMAIL;

public class BookFixture {

    public static String NAME = "플로니";
    public static String URL = "https://fileisHere.com";
    public static String UPDATE_URL = "https://fileisUpdate.com";
    public static String BOOK_KEY = "book-key";


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
            .owner(DEFAULT_EMAIL)
            .code(CodeFactory.generateCode())
            .userCapacity(BookUserCapacity.DEFAULT.getValue())
            .build();
    }

    public static CodeJoinRequest codeJoinRequest() {
        return new CodeJoinRequest(CodeFactory.generateCode());
    }

    public static Book createBook() {
        return Book.builder()
            .name(NAME)
            .profileImg(URL)
            .bookKey(BOOK_KEY)
            .owner(DEFAULT_EMAIL)
            .code(CodeFactory.generateCode())
            .userCapacity(BookUserCapacity.DEFAULT.getValue())
            .build();
    }

    public static Book createBookWithOwner(final String owner) {
        return Book.builder()
            .name(NAME)
            .profileImg(URL)
            .bookKey(BOOK_KEY)
            .owner(owner)
            .code(CodeFactory.generateCode())
            .userCapacity(BookUserCapacity.DEFAULT.getValue())
            .build();
    }

    public static Book createBookWithCode(final String code) {
        return Book.builder()
            .name(NAME)
            .profileImg(URL)
            .bookKey(BOOK_KEY)
            .owner(DEFAULT_EMAIL)
            .code(code)
            .userCapacity(BookUserCapacity.DEFAULT.getValue())
            .build();
    }

    public static OurBookUser createOurBookUser() {
        return OurBookUser.builder()
            .name(NAME)
            .profileImg(URL)
            .email(DEFAULT_EMAIL)
            .build();
    }

    public static UpdateBookImgRequest updateBookImgRequest() {
        return UpdateBookImgRequest.builder()
            .bookKey(BOOK_KEY)
            .newUrl(UPDATE_URL)
            .build();
    }
}

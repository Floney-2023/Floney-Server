package com.floney.floney.book;

import com.floney.floney.book.dto.CreateBookRequest;

public class BookFixture {

    private static String NAME = "플로니";
    private static String EMAIL = "floney@naver.com";

    private static String URL = "https://fileisHere.com";

    public static CreateBookRequest createBookRequest() {
        return CreateBookRequest.builder()
            .name(NAME)
            .email(EMAIL)
            .profileImg(URL)
            .build();
    }

}

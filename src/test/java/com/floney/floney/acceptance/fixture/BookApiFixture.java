package com.floney.floney.acceptance.fixture;

import com.floney.floney.book.dto.request.CodeJoinRequest;
import com.floney.floney.book.dto.request.CreateBookRequest;
import com.floney.floney.book.dto.response.CreateBookResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class BookApiFixture {

    public static CreateBookResponse createBook(final String accessToken) {
        final CreateBookRequest request = CreateBookRequest.builder()
            .name("name")
            .profileImg("url")
            .build();

        return RestAssured
            .given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().post("/books")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().as(CreateBookResponse.class);
    }

    public static CreateBookResponse involveBook(final String accessToken, final String code) {
        final CodeJoinRequest request = new CodeJoinRequest(code);

        return RestAssured
            .given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().post("/books/join")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .extract().as(CreateBookResponse.class);
    }
}

package com.floney.floney.acceptance.fixture;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.dto.request.CodeJoinRequest;
import com.floney.floney.book.dto.request.CreateBookRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;

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

    public static TotalDayLinesResponse getBookLineByDay(String token, String date, String bookKey) {
        return RestAssured
            .given()
            .header("Authorization", "Bearer " + token)
            .param("bookKey", bookKey)
            .param("date", date)
            .when().get("/books/days")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(TotalDayLinesResponse.class);
    }

    public static BookLineResponse createBookLineWith(final String accessToken, final String bookKey, String lineCategory, String subCategory, String assetSubCategory, LocalDate localDate) {
        final BookLineRequest request = BookLineRequest.builder()
            .money(1000)
            .line(subCategory)
            .bookKey(bookKey)
            .flow(lineCategory)
            .asset(assetSubCategory)
            .lineDate(localDate)
            .description("예시")
            .except(false)
            .repeatDuration(RepeatDuration.NONE)
            .build();

        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().post("/books/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().as(BookLineResponse.class);
    }
}

package com.floney.floney.acceptance.fixture;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.RepeatBookLineResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;

public class BookApiFixture {

    public static CreateBookResponse createBook(final String accessToken) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "name": "name",
                    "profileImg": "url"
                }
                """)
            .when().post("/books")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().as(CreateBookResponse.class);
    }

    public static CreateBookResponse involveBook(final String accessToken, final String code) {
        return RestAssured
            .given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "code": "%s"
                }
                """.formatted(code))
            .when().post("/books/join")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .extract().as(CreateBookResponse.class);
    }

    public static RepeatBookLineResponse[] getRepeatBookLineList(final String accessToken,
                                                                 final CategoryType categoryType,
                                                                 final String bookKey) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("categoryType", categoryType)
            .param("bookKey", bookKey)
            .when().get("/books/repeat")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(RepeatBookLineResponse[].class);
    }

    public static long createBookLine(final String accessToken,
                                      final String bookKey,
                                      final String lineCategoryName,
                                      final String lineSubcategoryName,
                                      final String assetSubcategoryName) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "bookKey": "%s",
                    "money": 1000,
                    "flow": "%s",
                    "asset": "%s",
                    "line": "%s",
                    "lineDate": "2000-01-01",
                    "except": false,
                    "repeatDuration": "NONE"
                }
                """.formatted(bookKey, lineCategoryName, assetSubcategoryName, lineSubcategoryName))
            .when().post("/books/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .jsonPath()
            .getLong("id");
    }

    public static long createBookLine(final String accessToken,
                                      final String bookKey,
                                      final String flow) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "bookKey": "%s",
                    "money": 1000,
                    "flow": "%s",
                    "asset": "체크카드",
                    "line": "급여",
                    "lineDate": "2000-01-01",
                    "except": false,
                    "repeatDuration": "NONE"
                }
                """.formatted(bookKey, flow))
            .when().post("/books/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .jsonPath()
            .getLong("id");
    }

    public static long createBookLine(final String accessToken,
                                      final String bookKey,
                                      final String lineCategoryName,
                                      final String lineSubcategoryName,
                                      final String assetSubcategoryName,
                                      final LocalDate date) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "bookKey": "%s",
                    "money": 1000,
                    "flow": "%s",
                    "asset": "%s",
                    "line": "%s",
                    "lineDate": "%s",
                    "except": false,
                    "repeatDuration": "NONE"
                }
                """.formatted(bookKey, lineCategoryName, assetSubcategoryName, lineSubcategoryName, date))
            .when().post("/books/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .jsonPath()
            .getLong("id");
    }

    public static long createBookLine(final String accessToken,
                                      final String bookKey,
                                      final String lineCategoryName,
                                      final String lineSubcategoryName,
                                      final String assetSubcategoryName,
                                      final RepeatDuration repeatDuration) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "bookKey": "%s",
                    "money": 1000,
                    "flow": "%s",
                    "asset": "%s",
                    "line": "%s",
                    "lineDate": "2000-01-01",
                    "except": false,
                    "repeatDuration": "%s"
                }
                """.formatted(bookKey, lineCategoryName, assetSubcategoryName, lineSubcategoryName, repeatDuration))
            .when().post("/books/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .jsonPath()
            .getLong("id");
    }

    public static long createBookLine(final String accessToken,
                                      final String bookKey,
                                      final String lineCategoryName,
                                      final String lineSubcategoryName,
                                      final String assetSubcategoryName,
                                      final LocalDate lineDate,
                                      final RepeatDuration repeatDuration) {
        return RestAssured.given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("""
                {
                    "bookKey": "%s",
                    "money": 1000,
                    "flow": "%s",
                    "asset": "%s",
                    "line": "%s",
                    "lineDate": "%s",
                    "except": false,
                    "repeatDuration": "%s"
                }
                """.formatted(bookKey, lineCategoryName, assetSubcategoryName, lineSubcategoryName, lineDate, repeatDuration))
            .when().post("/books/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .jsonPath()
            .getLong("id");
    }

    public static TotalDayLinesResponse getBookLineByDay(String token, String date, String bookKey) {
        return RestAssured
                .given()
                .auth().oauth2(token)
                .param("bookKey", bookKey)
                .param("date", date)
                .when().get("/books/days")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TotalDayLinesResponse.class);
    }
}

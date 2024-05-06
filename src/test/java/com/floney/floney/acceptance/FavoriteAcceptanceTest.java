package com.floney.floney.acceptance;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.FavoriteApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.fixture.UserFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;

@AcceptanceTest
@DisplayName("인수 테스트: 즐겨찾기")
public class FavoriteAcceptanceTest {

    @Nested
    @DisplayName("즐겨찾기를 추가할 때")
    class Describe_createFavorite {

        @Nested
        @DisplayName("description 을 제외한 모든 값이 주어진 경우")
        class Context_With_RequestWithoutDescription {

            String accessToken;
            String bookKey;

            final double money = 10000;
            final String lineCategoryName = "지출";
            final String lineSubcategoryName = "식비";
            final String assetSubcategoryName = "현금";

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("즐겨찾기가 생성된다.")
            void it_returns_favorite() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "money": %s,
                            "lineCategoryName": "%s",
                            "lineSubcategoryName": "%s",
                            "assetSubcategoryName": "%s"
                        }
                        """.formatted(money, lineCategoryName, lineSubcategoryName, assetSubcategoryName))
                    .when()
                    .post("/books/{key}/favorites", bookKey)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(
                        "id", notNullValue(),
                        "money", is((float) money),
                        "description", nullValue(),
                        "lineCategoryName", is(lineCategoryName),
                        "lineSubcategoryName", is(lineSubcategoryName),
                        "assetSubcategoryName", is(assetSubcategoryName)
                    );
            }
        }

        @Nested
        @DisplayName("이미 해당 가계부에 10개의 즐겨찾기가 존재하는 경우")
        class Context_With_10Favorites {

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                for (int i = 0; i < 10; i++) {
                    FavoriteApiFixture.createFavorite(accessToken, bookKey);
                }
            }

            @Test
            @DisplayName("에러가 발생한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "money": 10000,
                            "lineCategoryName": "지출",
                            "lineSubcategoryName": "식비",
                            "assetSubcategoryName": "현금"
                        }
                        """)
                    .when()
                    .post("/books/{key}/favorites", bookKey)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(
                        "code", is(ErrorType.INVALID_FAVORITE_SIZE.getCode()),
                        "message", is(ErrorType.INVALID_FAVORITE_SIZE.getMessage())
                    );
            }
        }

        @Nested
        @DisplayName("특정 값 없이 요청한 경우")
        class Context_With_InvalidRequest {

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("에러가 발생한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "lineCategoryName": "",
                            "lineSubcategoryName": "",
                            "assetSubcategoryName": ""
                        }
                        """)
                    .when()
                    .post("/books/{key}/favorites", bookKey)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(
                        "code", is("1"),
                        "message", containsString("입력해주세요")
                    );
            }
        }
    }

    @Nested
    @DisplayName("즐겨찾기를 조회할 때")
    class Describe_GetFavorite {

        @Nested
        @DisplayName("존재하는 즐겨찾기의 id로 요청한 경우")
        class Context_With_FavoriteIdExists {

            int favoriteId;
            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                favoriteId = RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "money": 1000,
                            "lineCategoryName": "지출",
                            "lineSubcategoryName": "식비",
                            "assetSubcategoryName": "현금"
                        }
                        """)
                    .when()
                    .post("/books/{key}/favorites", bookKey)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .extract().path("id");
            }

            @Test
            @DisplayName("즐겨찾기 정보를 반환한다.")
            void it_returns_favoriteInfo() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .when()
                    .get("/books/{key}/favorites/{id}", bookKey, favoriteId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(
                        "id", is(favoriteId),
                        "money", is(1000.0f),
                        "description", nullValue(),
                        "lineCategoryName", is("지출"),
                        "lineSubcategoryName", is("식비"),
                        "assetSubcategoryName", is("현금")
                    );
            }
        }

        @Nested
        @DisplayName("존재하지 않는 즐겨찾기의 id로 요청한 경우")
        class Context_With_FavoriteNotExist {

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("에러가 발생한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .when()
                    .get("/books/{key}/favorites/{id}", bookKey, 1)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(
                        "code", is(ErrorType.FAVORITE_NOT_FOUND.getCode()),
                        "message", is(ErrorType.FAVORITE_NOT_FOUND.getMessage())
                    );
            }
        }
    }
}

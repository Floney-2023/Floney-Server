package com.floney.floney.acceptance;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.FavoriteApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.exception.common.ErrorType;
import com.floney.floney.fixture.UserFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.*;

@AcceptanceTest
@DisplayName("인수 테스트: 즐겨찾기")
public class FavoriteAcceptanceTest {

    @Nested
    @DisplayName("즐겨찾기를 추가할때")
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
            final boolean exceptStatus = true;

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
                            "assetSubcategoryName": "%s",
                            "exceptStatus": %s
                        }
                        """.formatted(money, lineCategoryName, lineSubcategoryName, assetSubcategoryName, exceptStatus))
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
                        "assetSubcategoryName", is(assetSubcategoryName),
                        "exceptStatus", is(exceptStatus)
                    );
            }
        }

        @Nested
        @DisplayName("이미 해당 가계부 및 카테고리에 5개의 즐겨찾기가 존재하는 경우")
        class Context_With_AllFavorites {

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                for (int i = 0; i < 5; i++) {
                    FavoriteApiFixture.createFavoriteByLineCategory(accessToken, bookKey, "지출", "식비");
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
                            "assetSubcategoryName": "현금",
                            "exceptStatus": false
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
                            "assetSubcategoryName": "현금",
                            "exceptStatus": false
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
                        "assetSubcategoryName", is("현금"),
                        "exceptStatus", is(false)
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

    @Nested
    @DisplayName("lineCategory 별로 즐겨찾기를 조회할 때")
    class Describe_GetFavoritesByLineCategory {

        @Nested
        @DisplayName("존재하는 lineCategory로 요청한 경우")
        class Context_With_LineCategoryExists {

            String accessToken;
            String bookKey;
            List<Integer> favoriteIds;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();

                // 해당 lineCategory 로 즐겨찾기 생성
                final int favoriteSize = 3;
                favoriteIds = new ArrayList<>(favoriteSize);
                for (int i = 0; i < favoriteSize; i++) {
                    final int favoriteId = FavoriteApiFixture.createFavoriteByLineCategory(accessToken, bookKey, "수입", "급여");
                    favoriteIds.add(favoriteId);
                }
                favoriteIds.sort(Comparator.reverseOrder());
                // 다른 lineCategory 로 즐겨찾기 생성
                FavoriteApiFixture.createFavoriteByLineCategory(accessToken, bookKey, "지출", "식비");
            }

            @Test
            @DisplayName("정렬된 즐겨찾기 목록을 반환한다.")
            void it_returns_favorites() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .param("categoryType", CategoryType.INCOME)
                    .when()
                    .get("/books/{key}/favorites", bookKey)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(
                        "size()", is(favoriteIds.size()),
                        "findAll { it.lineCategoryName == '수입' }.size()", is(favoriteIds.size()),
                        "[0].id", is(favoriteIds.get(0)),
                        "[1].id", is(favoriteIds.get(1)),
                        "[2].id", is(favoriteIds.get(2))
                    );
            }
        }

        @Nested
        @DisplayName("존재하지 않는 lineCategory로 요청한 경우")
        class Context_With_LineCategoryNotExist {

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
                    .param("categoryType", CategoryType.ASSET)
                    .when()
                    .get("/books/{key}/favorites", bookKey)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(
                        "code", is(ErrorType.NOT_FOUND_CATEGORY.getCode()),
                        "message", is(ErrorType.NOT_FOUND_CATEGORY.getMessage())
                    );
            }
        }
    }

    @Nested
    @DisplayName("즐겨찾기를 삭제할 때")
    class Describe_DeleteFavorite {

        @Nested
        @DisplayName("존재하는 즐겨찾기의 id로 요청한 경우")
        class Context_With_ValidFavorite {

            int favoriteId;
            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                favoriteId = FavoriteApiFixture.createFavorite(accessToken, bookKey);
            }

            @Test
            @DisplayName("즐겨찾기가 삭제된다.")
            void it_deletes_favorite() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .when()
                    .delete("/books/{key}/favorites/{id}", bookKey, favoriteId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .when()
                    .get("/books/{key}/favorites/{id}", bookKey, favoriteId)
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

package com.floney.floney.acceptance;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.exception.common.ErrorResponse;
import com.floney.floney.favorite.dto.MyFavoriteResponseByFlow;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("인수테스트 : 즐겨찾기")
@AcceptanceTest
public class FavoriteAcceptanceTest {

    @Nested
    @DisplayName("즐겨찾기 등록 요청할 때")
    class Describe_RegisterMyFavorite {

        @Nested
        @DisplayName("요청이 유효할 경우")
        class Context_With_ValidRegisterFavoriteRequest {

            final User user = UserFixture.emailUser();
            private String token;
            private final CategoryType flow = CategoryType.INCOME;
            private long bookLine1Id;
            private long bookLine2Id;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                bookLine1Id = BookApiFixture.createBookLine(token, bookKey, flow.getMeaning());
                bookLine2Id = BookApiFixture.createBookLine(token, bookKey, flow.getMeaning());
            }

            @Test
            @DisplayName("즐겨찾기가 등록된다.")
            void it_succeeds() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/users/favorites/{bookLineId}", bookLine1Id)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/users/favorites/{bookLineId}", bookLine2Id)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

                MyFavoriteResponseByFlow[] responses = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/users/favorites/{flow}", flow)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MyFavoriteResponseByFlow[].class);

                assertThat(responses)
                    .extracting("bookLineId")
                    .containsExactly(bookLine2Id, bookLine1Id);
            }
        }

        @Nested
        @DisplayName("이미 등록된 즐겨찾기일 경우")
        class Context_With_AlreadyRegisteredFavoriteRequest {

            final User user = UserFixture.emailUser();
            private String token;
            private final CategoryType flow = CategoryType.INCOME;
            private long bookLineId;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                bookLineId = BookApiFixture.createBookLine(token, bookKey, flow.getMeaning());
                // TODO APIFixture로 분리
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/users/favorites/{bookLineId}", bookLineId);
            }

            @Test
            @DisplayName("에러가 발생한다.")
            void it_succeeds() {

                ErrorResponse response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/users/favorites/{bookLineId}", bookLineId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract().as(ErrorResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("message", "이미 즐겨찾기로 등록되어 있는 가계부 내역입니다.")
                    .hasFieldOrPropertyWithValue("code", "F001");

            }
        }

    }

    @Nested
    @DisplayName("즐겨찾기 조회 요청할 때")
    class Describe_ShowMyFavorite {

        @Nested
        @DisplayName("요청이 유효할 경우")
        class Context_With_ValidShowMyFavoritesRequest {

            final User user = UserFixture.emailUser();
            private String token;
            private final CategoryType flow = CategoryType.INCOME;
            private long bookLineId;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                bookLineId = BookApiFixture.createBookLine(token, bookKey, flow.getMeaning());
                // TODO APIFixture로 분리
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/users/favorites/{bookLineId}", bookLineId);
            }

            @Test
            @DisplayName("즐겨찾기가 조회된다.")
            void it_succeeds() {

                MyFavoriteResponseByFlow[] responses = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/users/favorites/{flow}", flow)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MyFavoriteResponseByFlow[].class);

                assertThat(responses).extracting("bookLineId")
                    .containsOnly(bookLineId);

            }
        }

    }

    @Nested
    @DisplayName("즐겨찾기 취소 요청할 때")
    class Describe_DeleteMyFavorite {

        @Nested
        @DisplayName("요청이 유효할 경우")
        class Context_With_ValidDeleteMyFavoritesRequest {

            final User user = UserFixture.emailUser();
            private String token;
            private final CategoryType flow = CategoryType.INCOME;
            private long bookLineId;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                bookLineId = BookApiFixture.createBookLine(token, bookKey, flow.getMeaning());
                // TODO APIFixture로 분리
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/users/favorites/{bookLineId}", bookLineId);
            }

            @Test
            @DisplayName("즐겨찾기가 취소된다.")
            void it_succeeds() {

                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().delete("/users/favorites/{bookLineId}", bookLineId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

                MyFavoriteResponseByFlow[] responses = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/users/favorites/{flow}", flow)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MyFavoriteResponseByFlow[].class);

                assertThat(responses).extracting("bookLineId")
                    .doesNotContain(bookLineId);

            }
        }

    }

}

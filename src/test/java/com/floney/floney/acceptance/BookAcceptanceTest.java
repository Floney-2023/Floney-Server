package com.floney.floney.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.dto.request.CarryOverRequest;
import com.floney.floney.book.dto.request.SeeProfileRequest;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.dto.request.UpdateBudgetRequest;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.InvolveBookResponse;
import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.common.ErrorResponse;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("인수 테스트: 가계부")
@AcceptanceTest
public class BookAcceptanceTest {

    @Nested
    @DisplayName("가계부 이미지를 변경할 때")
    class Describe_UpdateBookImg {

        @Nested
        @DisplayName("가계부가 존재할 경우")
        class Context_With_BookExists {

            final User user = UserFixture.emailUser();

            Token token;
            String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                bookKey = BookApiFixture.createBook(token.getAccessToken()).getBookKey();
            }

            @Test
            @DisplayName("성공한다.")
            void it_succeeds() {
                final UpdateBookImgRequest request = UpdateBookImgRequest.builder()
                        .bookKey(bookKey)
                        .newUrl(BookFixture.UPDATE_URL)
                        .build();

                RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/bookImg")
                        .then()
                        .statusCode(HttpStatus.OK.value());
            }
        }

        @Nested
        @DisplayName("가계부가 존재하지 않을 경우")
        class Context_With_NoBook {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final UpdateBookImgRequest request = UpdateBookImgRequest.builder()
                        .bookKey("invalid-key")
                        .newUrl(BookFixture.UPDATE_URL)
                        .build();

                final ErrorResponse response = RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/bookImg")
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .extract().as(ErrorResponse.class);

                assertThat(response)
                        .hasFieldOrPropertyWithValue("code", "B001")
                        .hasFieldOrPropertyWithValue("message", "가계부가 존재하지 않습니다");
            }
        }

    }

    @Nested
    @DisplayName("유저 이미지 공개 여부를 변경할 때")
    class Describe_ChangeSeeProfile {

        @Nested
        @DisplayName("가계부가 존재할 경우")
        class Context_With_BookExists {

            final User user = UserFixture.emailUser();

            Token token;
            String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                bookKey = BookApiFixture.createBook(token.getAccessToken()).getBookKey();
            }

            @Test
            @DisplayName("성공한다.")
            void it_succeeds() {
                final SeeProfileRequest request =
                        new SeeProfileRequest(bookKey, Boolean.FALSE);

                RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/seeProfile")
                        .then()
                        .statusCode(HttpStatus.OK.value());
            }
        }

        @Nested
        @DisplayName("가계부가 존재하지 않을 경우")
        class Context_With_NoBook {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final SeeProfileRequest request =
                        new SeeProfileRequest("invalid-key", Boolean.FALSE);

                final ErrorResponse response = RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/seeProfile")
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .extract().as(ErrorResponse.class);

                assertThat(response)
                        .hasFieldOrPropertyWithValue("code", "B001")
                        .hasFieldOrPropertyWithValue("message", "가계부가 존재하지 않습니다");
            }
        }

    }

    @Nested
    @DisplayName("이월 내역 설정을 변경할 때")
    class Describe_ChangeCarryOver {

        @Nested
        @DisplayName("가계부가 존재할 경우")
        class Context_With_BookExists {

            final User user = UserFixture.emailUser();

            Token token;
            String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                bookKey = BookApiFixture.createBook(token.getAccessToken()).getBookKey();
            }

            @Test
            @DisplayName("성공한다.")
            void it_succeeds() {
                final CarryOverRequest request = new CarryOverRequest(
                        Boolean.FALSE,
                        bookKey
                );

                RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/carryOver")
                        .then()
                        .statusCode(HttpStatus.OK.value());
            }
        }

    }

    @Nested
    @DisplayName("예산을 변경할 때")
    class Describe_UpdateBudget {

        @Nested
        @DisplayName("가계부가 존재할 경우")
        class Context_With_BookExists {

            final User user = UserFixture.emailUser();

            Token token;
            String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                bookKey = BookApiFixture.createBook(token.getAccessToken()).getBookKey();
            }

            @Test
            @DisplayName("성공한다.")
            void it_succeeds() {
                final UpdateBudgetRequest request = new UpdateBudgetRequest(
                        bookKey,
                        LocalDate.now(),
                        10000
                );

                RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/budget")
                        .then()
                        .statusCode(HttpStatus.OK.value());
            }
        }

        @Nested
        @DisplayName("가계부가 존재하지 않을 경우")
        class Context_With_NoBook {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final UpdateBudgetRequest request = new UpdateBudgetRequest(
                        "invalid-key",
                        LocalDate.now(),
                        10000
                );
                final ErrorResponse response = RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(request)
                        .when().post("/books/info/budget")
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .extract().as(ErrorResponse.class);

                assertThat(response)
                        .hasFieldOrPropertyWithValue("code", "B001")
                        .hasFieldOrPropertyWithValue("message", "가계부가 존재하지 않습니다");
            }
        }

    }

    @Nested
    @DisplayName("유저 가계부 유효 확인할 때")
    class Describe_FindInvolveBook {

        @Nested
        @DisplayName("유저가 참여하는 가계부가 존재할 경우")
        class Context_With_InvolveBookExists {

            final User user = UserFixture.emailUser();

            Token token;
            String lastAccessedBookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                BookApiFixture.createBook(token.getAccessToken());
                lastAccessedBookKey = BookApiFixture.createBook(token.getAccessToken()).getBookKey();
            }

            @Test
            @DisplayName("유저가 가장 최근에 접근한 가계부의 식별 키를 응답한다.")
            void it_responses_bookKey() {

                InvolveBookResponse response = RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("/books/users/check")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(InvolveBookResponse.class);

                assertThat(response)
                        .hasFieldOrPropertyWithValue("bookKey", lastAccessedBookKey);

            }
        }

        @Nested
        @DisplayName("유저가 참여하는 가계부가 존재하지 않을 경우")
        class Context_With_InvolveBookNotExists {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("null을 응답한다.")
            void it_responses_null() {

                InvolveBookResponse response = RestAssured
                        .given()
                        .auth().oauth2(token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("/books/users/check")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(InvolveBookResponse.class);

                assertThat(response)
                        .hasFieldOrPropertyWithValue("bookKey", null);
            }
        }

    }

    @Nested
    @DisplayName("가계부의 모든 유저들을 조회할 때")
    class Describe_FindUsersByBook {

        @Nested
        @DisplayName("가계부에 유저가 존재할 경우 ")
        class Context_With_InvolveBookExists {

            final String mail1 = "floney1@gmail.com";
            final String mail2 = "floney2@gmail.com";
            final String mail3 = "floney3@gmail.com";
            final User user1 = UserFixture.emailUserWithEmail(mail1);
            final User user2 = UserFixture.emailUserWithEmail(mail2);
            final User user3 = UserFixture.emailUserWithEmail(mail3);

            Token token1;
            Token token2;
            Token token3;
            CreateBookResponse createBookResponse;

            @BeforeEach
            public void init() {
                token1 = UserApiFixture.loginAfterSignup(user1);
                token2 = UserApiFixture.loginAfterSignup(user2);
                token3 = UserApiFixture.loginAfterSignup(user3);
                createBookResponse = BookApiFixture.createBook(token1.getAccessToken());
                BookApiFixture.involveBook(token2.getAccessToken(), createBookResponse.getCode());
                BookApiFixture.involveBook(token3.getAccessToken(), createBookResponse.getCode());
            }

            @Test
            @DisplayName("성공한다.")
            void it_succeeds() {

                java.util.List<?> responses = RestAssured
                        .given()
                        .log().all()
                        .auth().oauth2(token1.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .params("bookKey", createBookResponse.getBookKey())
                        .when().get("/books/users")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.OK.value())
                        .extract().as(java.util.List.class);

                assertThat(responses)
                        .extracting("email")
                        .containsExactly(mail1, mail2, mail3);
            }

        }

    }
}



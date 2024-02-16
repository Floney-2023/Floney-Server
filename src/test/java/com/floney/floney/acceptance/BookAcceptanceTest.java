package com.floney.floney.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.common.ErrorResponse;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import io.restassured.RestAssured;
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

    // TODO 내역 조회 시 유저 이미지 공개 여부 변경

    // TODO 이월 내역 설정 변경

    // TODO 예산 변경

    // TODO 유저 가계부 유효 확인

    // TODO 가계부의 모든 유저들 조회
}



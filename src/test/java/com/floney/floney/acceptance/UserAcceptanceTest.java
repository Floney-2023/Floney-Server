package com.floney.floney.acceptance;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.common.ErrorResponse;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.dto.request.SignoutRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.request.UpdatePasswordRequest;
import com.floney.floney.user.dto.response.MyPageResponse;
import com.floney.floney.user.dto.response.SignoutResponse;
import com.floney.floney.user.entity.User;
import io.restassured.RestAssured;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("인수 테스트: 회원")
@AcceptanceTest
class UserAcceptanceTest {

    @Nested
    @DisplayName("회원 가입할 때")
    class Describe_Signup {

        @Nested
        @DisplayName("이메일, 비밀번호, 닉네임, 마케팅 수신 동의 여부를 알맞게 작성한 경우")
        class Context_With_ValidSignupRequest {

            final User user = UserFixture.emailUser();

            final SignupRequest request = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .receiveMarketing(user.isReceiveMarketing())
                .build();

            @Test
            @DisplayName("토큰을 반환한다.")
            void it_returns_token() {
                final Token response = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/users")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().as(Token.class);

                assertThat(response)
                    .hasFieldOrProperty("accessToken")
                    .hasFieldOrProperty("refreshToken")
                    .hasNoNullFieldsOrProperties();
            }
        }

        @Nested
        @DisplayName("이메일을 작성하지 않은 경우")
        class Context_With_NoEmail {

            final User user = UserFixture.emailUser();

            final SignupRequest request = SignupRequest.builder()
                .password(user.getPassword())
                .nickname(user.getNickname())
                .receiveMarketing(user.isReceiveMarketing())
                .build();


            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final ErrorResponse response = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/users")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract().as(ErrorResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("code", "1")
                    .hasFieldOrPropertyWithValue("message", "이메일을 입력해주세요");
            }
        }

        @Nested
        @DisplayName("이미 가입한 경우")
        class Context_With_DuplicateUser {

            final User user = UserFixture.emailUser();

            final SignupRequest request = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .receiveMarketing(user.isReceiveMarketing())
                .build();

            @BeforeEach
            public void init() {
                UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final Map<String, Object> response = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/users")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract().as(Map.class);

                assertThat(response)
                    .containsEntry("code", "U001")
                    .containsEntry("message", "이미 존재하는 유저입니다")
                    .containsEntry("provider", Provider.EMAIL.name());
            }
        }

        @Nested
        @DisplayName("닉네임을 작성하지 않은 경우")
        class Context_With_NoNickname {

            final User user = UserFixture.emailUser();

            final SignupRequest request = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .receiveMarketing(user.isReceiveMarketing())
                .build();

            final ErrorResponse response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/users")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().as(ErrorResponse.class);

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                assertThat(response)
                    .hasFieldOrPropertyWithValue("code", "1")
                    .hasFieldOrPropertyWithValue("message", "닉네임을 입력해주세요");
            }
        }

        @Nested
        @DisplayName("마케팅 수신 동의 여부를 작성하지 않은 경우")
        class Context_With_NoReceiveMarketing {

            final User user = UserFixture.emailUser();

            final SignupRequest request = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .build();

            final ErrorResponse response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/users")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract().as(ErrorResponse.class);

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                assertThat(response)
                    .hasFieldOrPropertyWithValue("code", "1")
                    .hasFieldOrPropertyWithValue("message", "마케팅 수신 동의 여부를 입력해주세요");
            }
        }
    }

    @Nested
    @DisplayName("회원 탈퇴할 때")
    class Describe_Signout {

        @Nested
        @DisplayName("참여한 가계부가 없는 경우")
        class Context_With_NoBook {

            final User user = UserFixture.emailUser();
            final SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("bookKey를 반환하지 않는다.")
            void it_does_not_return() {
                final SignoutResponse response = RestAssured
                    .given()
                    .param("accessToken", token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().delete("/users")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(SignoutResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("deletedBookKeys", List.of())
                    .hasFieldOrPropertyWithValue("otherBookKeys", List.of());
            }
        }

        @Nested
        @DisplayName("가계부에 혼자만 참여한 경우")
        class Context_With_BookWithNoOne {

            final User user = UserFixture.emailUser();
            final SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

            Token token;
            CreateBookResponse createBookResponse;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                createBookResponse = BookApiFixture.createBook(token.getAccessToken());
            }

            @Test
            @DisplayName("가계부를 삭제하고 bookKey를 반환한다.")
            void it_returns_bookKey() {
                final SignoutResponse response = RestAssured
                    .given()
                    .param("accessToken", token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().delete("/users")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(SignoutResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("deletedBookKeys", List.of(createBookResponse.getBookKey()))
                    .hasFieldOrPropertyWithValue("otherBookKeys", List.of());
            }
        }

        @Nested
        @DisplayName("방장인 가계부에 다른 사람도 참여한 경우")
        class Context_With_BookWithOthers {

            final User user = UserFixture.emailUser();
            final SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

            Token token;
            CreateBookResponse createBookResponse;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                createBookResponse = BookApiFixture.createBook(token.getAccessToken());

                // 다른 사용자가 가계부에 참여
                final Token anotherToken = loginAfterSignup("test2@email.com");
                BookApiFixture.involveBook(anotherToken.getAccessToken(), createBookResponse.getCode());
            }

            @Test
            @DisplayName("가계부를 삭제하지 않고 bookKey를 반환한다.")
            void it_returns_bookKey() {
                final SignoutResponse response = RestAssured
                    .given()
                    .param("accessToken", token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().delete("/users")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(SignoutResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("deletedBookKeys", List.of())
                    .hasFieldOrPropertyWithValue("otherBookKeys", List.of(createBookResponse.getBookKey()));
            }
        }

        @Nested
        @DisplayName("참여한 가계부가 있는 경우")
        class Context_With_NotBookOwner {

            final User user = UserFixture.emailUser();
            final SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

            Token token;
            CreateBookResponse createBookResponse;

            @BeforeEach
            public void init() {
                // 다른 사용자가 가계부 생성
                createBookResponse = signupAndCreateBook("test2@email.com");

                // 다른 사용자가 방장인 가계부에 참여
                token = UserApiFixture.loginAfterSignup(user);
                BookApiFixture.involveBook(token.getAccessToken(), createBookResponse.getCode());
            }

            @Test
            @DisplayName("가계부를 삭제하지 않고 bookKey를 반환한다.")
            void it_returns_bookKey() {
                final SignoutResponse response = RestAssured
                    .given()
                    .param("accessToken", token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().delete("/users")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(SignoutResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("deletedBookKeys", List.of())
                    .hasFieldOrPropertyWithValue("otherBookKeys", List.of(createBookResponse.getBookKey()));
            }
        }

        @Nested
        @DisplayName("존재하지 않는 회원인 경우")
        class Context_With_UserNotExist {

            final User user = UserFixture.emailUser();
            final SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                UserApiFixture.signout(token.getAccessToken());
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final ErrorResponse response = RestAssured
                    .given()
                    .param("accessToken", token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().delete("/users")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .extract().as(ErrorResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("code", "U008")
                    .hasFieldOrPropertyWithValue("message", "해당 이메일로 가입된 유저가 없습니다");
            }
        }

        @Nested
        @DisplayName("기타 탈퇴 사유가 비어있는 경우")
        class Context_With_EmptySignoutOtherReason {

            final User user = UserFixture.emailUser();
            final SignoutRequest request = new SignoutRequest(SignoutType.OTHER, null);

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final ErrorResponse response = RestAssured
                    .given()
                    .param("accessToken", token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().delete("/users")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract().as(ErrorResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("code", "U019")
                    .hasFieldOrPropertyWithValue("message", "기타 탈퇴 사유가 없습니다");
            }
        }
    }

    @Nested
    @DisplayName("회원 정보를 불러올 때")
    class Describe_GetUserInfo {

        @Nested
        @DisplayName("참여한 가계부가 없는 경우")
        class Context_With_NoBook {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("빈 myBooks 리스트를 포함한 정보를 반환한다.")
            void it_returns_userInfo() {
                final MyPageResponse response = RestAssured
                    .given()
                    .auth().oauth2(token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/users/mypage")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MyPageResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("nickname", user.getNickname())
                    .hasFieldOrPropertyWithValue("email", user.getEmail())
                    .hasFieldOrPropertyWithValue("profileImg", user.getProfileImg())
                    .hasFieldOrPropertyWithValue("provider", user.getProvider())
                    .hasFieldOrProperty("lastAdTime")
                    .hasFieldOrPropertyWithValue("myBooks", List.of());
            }
        }

        @Nested
        @DisplayName("참여한 가계부가 있는 경우")
        class Context_With_BookExists {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
                BookApiFixture.createBook(token.getAccessToken());
            }

            @Test
            @DisplayName("비어있지 않은 myBooks 리스트를 포함한 정보를 반환한다.")
            void it_returns_userInfo() {
                final MyPageResponse response = RestAssured
                    .given()
                    .auth().oauth2(token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/users/mypage")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MyPageResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("nickname", user.getNickname())
                    .hasFieldOrPropertyWithValue("email", user.getEmail())
                    .hasFieldOrPropertyWithValue("profileImg", user.getProfileImg())
                    .hasFieldOrPropertyWithValue("provider", user.getProvider())
                    .hasFieldOrProperty("lastAdTime")
                    .extracting("myBooks")
                    .asInstanceOf(InstanceOfAssertFactories.list(MyBookInfo.class))
                    .hasSize(1);
            }
        }
    }

    @Nested
    @DisplayName("회원의 비밀번호를 변경할 때")
    class Describe_UpdatePassword {

        @Nested
        @DisplayName("기존과 다른 비밀번호인 경우")
        class Context_With_DifferentPassword {

            final User user = UserFixture.emailUser();
            final String newPassword = user.getPassword().concat("**");

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("성공한다.")
            void it_succeeds() {
                final UpdatePasswordRequest request = new UpdatePasswordRequest(newPassword);

                RestAssured
                    .given()
                    .auth().oauth2(token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().put("/users/password")
                    .then()
                    .statusCode(HttpStatus.OK.value());
            }
        }

        @Nested
        @DisplayName("기존과 동일한 비밀번호인 경우")
        class Context_With_SamePassword {

            final User user = UserFixture.emailUser();

            Token token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                final UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPassword());

                final ErrorResponse response = RestAssured
                    .given()
                    .auth().oauth2(token.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().put("/users/password")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .extract().as(ErrorResponse.class);

                assertThat(response)
                    .hasFieldOrPropertyWithValue("code", "U017")
                    .hasFieldOrPropertyWithValue("message", "이전 비밀번호와 같습니다");
            }
        }
    }

    private Token loginAfterSignup(final String email) {
        final User user = UserFixture.emailUserWithEmail(email);
        return UserApiFixture.loginAfterSignup(user);
    }

    private CreateBookResponse signupAndCreateBook(final String email) {
        return BookApiFixture.createBook(loginAfterSignup(email).getAccessToken());
    }
}


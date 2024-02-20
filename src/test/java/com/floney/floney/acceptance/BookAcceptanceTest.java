package com.floney.floney.acceptance;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.domain.Currency;
import com.floney.floney.book.domain.vo.MonthLinesResponse;
import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.InviteCodeResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.BookRequestDtoFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("인수 테스트 : 가계부")
@AcceptanceTest
public class BookAcceptanceTest {

    @Nested
    @DisplayName("addBook()을 실행할 때")
    class Describe_AddBook {

        @Nested
        @DisplayName("가계부 이름과 가계부 이미지를 작성한 경우")
        class Context_With_ValidCreateBookRequest {
            final CreateBookRequest request = BookFixture.createBookRequest();
            final User user = UserFixture.emailUser();
            private String token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
            }

            @Test
            @DisplayName("가계부 키와 참여 코드를 반환한다")
            void it_returns_book_key_and_code() {
                final CreateBookResponse response = RestAssured
                    .given()

                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().as(CreateBookResponse.class);

                assertThat(response)
                    .hasFieldOrProperty("bookKey")
                    .hasFieldOrProperty("code")
                    .hasNoNullFieldsOrProperties();
            }
        }

        @Nested
        @DisplayName("참여중인 가계부가 2개 이상인 경우")
        class Context_With_MaximumJoin {
            final CreateBookRequest request = BookFixture.createBookRequest();
            final User user = UserFixture.emailUser();
            private String token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                BookApiFixture.createBook(token);
                BookApiFixture.createBook(token);
            }

            @Test
            @DisplayName("제공하지 않는 서비스 예외를 발생한다")
            void it_returns_exception() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books")
                    .then()
                    .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                    .body("code", is("S002"));
            }
        }
    }

    @Nested
    @DisplayName("joinWithCode()을 실행할 때")
    class Describe_JoinWithCode {

        @Nested
        @DisplayName("유효한 가계부 참여 코드를 작성한 경우")
        class Context_With_ValidCodeJoinRequest {
            final User owner = UserFixture.emailUser();
            final User wantToJoinUser = UserFixture.kakaoUser();

            private CodeJoinRequest request;

            private String token;

            @BeforeEach
            public void init() {
                String codeFromSavedBook = BookApiFixture.createBook(UserApiFixture.loginAfterSignup(owner).getAccessToken()).getCode();

                token = UserApiFixture.loginAfterSignup(wantToJoinUser).getAccessToken();
                request = new CodeJoinRequest(codeFromSavedBook);
            }

            @Test
            @DisplayName("가계부 키와 참여 코드를 반환한다")
            void it_returns_book_key_and_code() {
                final CreateBookResponse response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/join")
                    .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract().as(CreateBookResponse.class);

                assertThat(response)
                    .hasFieldOrProperty("bookKey")
                    .hasFieldOrProperty("code")
                    .hasNoNullFieldsOrProperties();
            }
        }
    }

    @Nested
    @DisplayName("createBookLine()을 실행할 때")
    class Describe_CreateBookLine {

        @Nested
        @DisplayName("유효한 가계부 내역을 작성한 경우")
        class Context_With_ValidCodeJoinRequest {
            final User user = UserFixture.emailUser();
            private BookLineRequest request;
            private String token;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();

                String incomeLineCategory = "수입";
                String subCategory = "급여";
                String assetSubCategory = "체크카드";

                request = BookRequestDtoFixture.createBookLineRequest(bookKey, incomeLineCategory, subCategory, assetSubCategory);
            }

            @Test
            @DisplayName("작성한 내역을 반환한다")
            void it_returns_book_key_and_code() {
                final BookLineResponse response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/lines")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().as(BookLineResponse.class);

                assertThat(response)
                    .hasFieldOrProperty("money")
                    .hasFieldOrProperty("flow")
                    .hasNoNullFieldsOrProperties();
            }
        }
    }


    @Nested
    @DisplayName("showByMonth()을 실행할 때")
    class Describe_ShowByMonth {

        @Nested
        @DisplayName("해당월에 가계부 내역이 작성된 경우")
        class Context_With_BookLineInMonth {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();

                bookKey = BookApiFixture.createBook(token).getBookKey();

                String incomeLineCategory = "수입";
                String subCategory = "급여";
                String assetSubCategory = "체크카드";

                BookApiFixture.createBookLineWith(token, bookKey, incomeLineCategory, subCategory, assetSubCategory, LocalDate.of(2024, 2, 9));
                BookApiFixture.createBookLineWith(token, bookKey, incomeLineCategory, subCategory, assetSubCategory, LocalDate.of(2024, 2, 10));
            }

            @Test
            @DisplayName("월별 가계부 내역을 반환한다")
            public void it_returns_book() {
                final MonthLinesResponse response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .param("bookKey", bookKey)
                    .param("date", "2024-02-01")
                    .when().get("/books/month")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MonthLinesResponse.class);

                assertThat(response).hasFieldOrProperty("expenses");
                assertThat(response.getTotalIncome()).isEqualTo(2000.0);
                assertThat(response.getTotalOutcome()).isEqualTo(0.0);

            }
        }
    }

    @Nested
    @DisplayName("showByDays()을 실행할 때")
    class Describe_ShowByDays {

        @Nested
        @DisplayName("해당 일에 가계부 내역이 작성된 경우")
        class Context_With_ExistBookLine {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();

                String incomeLineCategory = "수입";
                String subCategory = "급여";
                String assetSubCategory = "체크카드";

                BookApiFixture.createBookLineWith(token, bookKey, incomeLineCategory, subCategory, assetSubCategory, LocalDate.of(2024, 2, 9));
                BookApiFixture.createBookLineWith(token, bookKey, incomeLineCategory, subCategory, assetSubCategory, LocalDate.of(2024, 2, 9));
            }

            @Test
            @DisplayName("일별가계부 내역을 반환한다")
            public void it_returns_bookLine() {
                final TotalDayLinesResponse response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .param("bookKey", bookKey)
                    .param("date", "2024-02-09")
                    .when().get("/books/days")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(TotalDayLinesResponse.class);

                assertThat(response).hasFieldOrProperty("dayLinesResponse")
                    .hasFieldOrProperty("totalExpense")
                    .hasFieldOrProperty("seeProfileImg")
                    .hasFieldOrProperty("carryOverInfo");

                assertThat(response.getDayLinesResponse().size()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("changeBookLine()을 실행할 때")
    class Describe_ChangeBookLine {
        @Nested
        @DisplayName("가계부 내역이 존재하는 경우")
        class Context_With_ExistBookLine {
            final User user = UserFixture.emailUser();
            private String token;
            private BookLineRequest request;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();

                String incomeLineCategory = "수입";
                String subCategory = "급여";

                String changeSubCategory = "용돈";
                String assetSubCategory = "체크카드";
                String date = "2024-02-14";

                BookApiFixture.createBookLineWith(token, bookKey, incomeLineCategory, subCategory, assetSubCategory, LocalDate.parse(date));
                long bookLineId = BookApiFixture.getBookLineByDay(token, date, bookKey).getDayLinesResponse().get(0).getId();

                request = BookLineRequest.builder()
                    .lineId(bookLineId)
                    .money(2000.0)
                    .bookKey(bookKey)
                    .line(changeSubCategory)
                    .flow(incomeLineCategory)
                    .lineDate(LocalDate.parse(date))
                    .except(false)
                    .asset(assetSubCategory)
                    .build();
            }

            @Test
            @DisplayName("가계부 내역을 수정한다")
            public void it_change_line() {
                final BookLineResponse response = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(token)
                    .body(request)
                    .when().post("/books/lines/change")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(BookLineResponse.class);

                assertThat(response.getMoney()).isEqualTo(2000.0);
            }
        }
    }

    @Nested
    @DisplayName("deleteBookLine()을 실행할 때")
    class Describe_DeleteBookLine {

        @Nested
        @DisplayName("가계부 내역이 존재하는 경우")
        class Context_With_ExistBookLine {
            final User user = UserFixture.emailUser();
            private String token;

            private long bookLineId;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();

                String incomeLineCategory = "수입";
                String subCategory = "급여";
                String assetSubCategory = "체크카드";

                String date = "2024-02-14";
                BookApiFixture.createBookLineWith(token, bookKey, incomeLineCategory, subCategory, assetSubCategory, LocalDate.parse(date));
                bookLineId = BookApiFixture.getBookLineByDay(token, date, bookKey).getDayLinesResponse().get(0).getId();
            }

            @Test
            @DisplayName("가계부 내역을 삭제한다")
            public void it_delete_line() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .param("bookLineKey", bookLineId)
                    .when().delete("/books/lines/delete")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();

            }
        }
    }

    @Nested
    @DisplayName("changeName()을 실행할 때")
    class Describe_ChangeName {

        @Nested
        @DisplayName("가계부 이름을 변경하는 경우")
        class Context_With_New_Name {
            final User user = UserFixture.emailUser();
            private String token;
            private BookNameChangeRequest request;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();

                String bookKey = BookApiFixture.createBook(token).getBookKey();
                request = new BookNameChangeRequest("새가계부이름", bookKey);
            }

            @Test
            @DisplayName("가계부 이름을 변경한다")
            public void it_changed_name() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/name")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            }
        }
    }

    @Nested
    @DisplayName("deleteBook()을 실행할 때")
    class Describe_DeleteBook {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_Exist_Book {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
            }

            @Test
            @DisplayName("가계부를 삭제한다")
            public void it_delete_book() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .when().delete("/books/delete")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            }
        }
    }

    @Nested
    @DisplayName("getMyBookInfo()을 실행할 때")
    class Describe_GetMyBookInfo {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_ExistBook {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
            }

            @Test
            @DisplayName("가계부 설정을 반환한다")
            public void it_returns_book_info() {
                OurBookInfo response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .when().get("/books/info")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(OurBookInfo.class);

                assertThat(response)
                    .hasFieldOrProperty("bookImg")
                    .hasFieldOrProperty("bookName")
                    .hasFieldOrProperty("startDay")
                    .hasFieldOrProperty("seeProfileStatus")
                    .hasFieldOrProperty("carryOver")
                    .hasFieldOrProperty("ourBookUsers")
                    .hasNoNullFieldsOrProperties();
            }
        }
    }

    @Nested
    @DisplayName("updateAsset()을 실행할 때")
    class Describe_UpdateAsset {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_Exist_Book {
            final User user = UserFixture.emailUser();
            private String token;
            private UpdateAssetRequest request;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                request = new UpdateAssetRequest(bookKey, 10.0, LocalDate.of(2024, 2, 4));

            }

            @Test
            @DisplayName("가계부 자산을 변경한다")
            public void it_update_book_asset() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/info/asset")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            }
        }
    }


    @Nested
    @DisplayName("deleteAll()을 실행할 때")
    class Describe_DeleteAll {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_ExistBook {
            final User user = UserFixture.emailUser();
            private String token;

            private String bookKey;


            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
            }

            @Test
            @DisplayName("가계부가 초기화된다")
            public void it_makes_book_init() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .when().delete("/books/info/delete/all")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            }
        }
    }


    @Nested
    @DisplayName("allOutcomes()을 실행할 때")
    class Describe_AllOutcomes {

        @Nested
        @DisplayName("지출 내역이 존재하는 경우")
        class Context_With_ExistOutcomes {
            private static final String outcomeLineCategory = "지출";
            private static final String subCategory = "식비";
            private static final String assetLineCategory = "자산";
            private static final String assetSubCategory = "체크카드";
            final User user = UserFixture.emailUser();
            private String token;
            private AllOutcomesRequest request;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();

                String bookKey = BookApiFixture.createBook(token).getBookKey();

                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);

                BookApiFixture.createBookLineWith(token, bookKey, outcomeLineCategory, subCategory, assetSubCategory, startDate);
                BookApiFixture.createBookLineWith(token, bookKey, outcomeLineCategory, subCategory, assetSubCategory, endDate);

                request = new AllOutcomesRequest(bookKey, Arrays.asList(user.getEmail()),
                    new DateDuration(startDate, endDate));
            }

            @Test
            @DisplayName("가계부의 모든 지출을 조회한다")
            public void it_returns_outcomes() {
                List response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/outcomes")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(List.class);

                assertThat(response.size()).isEqualTo(2);
            }
        }
    }

    @Nested
    @DisplayName("bookUserOut()을 실행할 때")
    class Describe_BookUserOut {

        @Nested
        @DisplayName("가계부에 유저가 참여하고 있는 경우")
        class Context_With_BookUserOut {
            final User user = UserFixture.emailUser();
            private String token;
            private BookUserOutRequest request;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                request = new BookUserOutRequest(bookKey);
            }

            @Test
            @DisplayName("가계부에서 나가진다")
            public void it_make_user_out() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/users/out")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            }
        }
    }

    @Nested
    @DisplayName("getInviteCode()를 실행할 때")
    class Describe_GetInviteCode {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_GetInviteCode {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
            }

            @Test
            @DisplayName("가계부의 초대 코드가 반환된다")
            public void it_returns_invite_code() {
                InviteCodeResponse response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .when().get("/books/code")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(InviteCodeResponse.class);

                assertThat(response)
                    .hasFieldOrProperty("code");
            }
        }
    }

    @Nested
    @DisplayName("changeCurrency()를 실행할 때")
    class Describe_ChangeCurrency {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_ExistBook {
            final User user = UserFixture.emailUser();
            private String token;
            private ChangeCurrencyRequest request;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                String bookKey = BookApiFixture.createBook(token).getBookKey();
                request = new ChangeCurrencyRequest(Currency.CNY, bookKey);
            }

            @Test
            @DisplayName("변경된 가계부의 통화가 반환된다")
            public void it_returns_change_currency() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when().post("/books/info/currency")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("myBookCurrency", is("CNY"));
            }
        }
    }

    @Nested
    @DisplayName("getCurrency()를 실행할 때")
    class Describe_GetCurrency {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_ExistBook {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
            }

            @Test
            @DisplayName("가계부의 통화가 반환된다")
            public void it_returns_currency() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .when().get("/books/info/currency")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("myBookCurrency", is("KRW"));
            }
        }
    }

    @Nested
    @DisplayName("getBookInfoByCode()를 실행할 때")
    class Describe_GetBookInfoByCode {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_Exist_Book {
            final User user = UserFixture.emailUser();
            private String token;
            private String code;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                code = BookApiFixture.createBook(token).getCode();
            }

            @Test
            @DisplayName("가계부의 정보가 반환된다")
            public void it_returns_book_info() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("code", code)
                    .when().get("/books")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract();
            }
        }
    }

    @Nested
    @DisplayName("getLastSettlement()를 실행할 때")
    class Describe_GetLastSettlement {

        @Nested
        @DisplayName("마지막 정산 날짜가 존재하지 않는 경우")
        class Context_With_Last_settlement {
            final User user = UserFixture.emailUser();
            private String token;

            private String bookKey;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
            }

            @Test
            @DisplayName("마지막 정산 날짜로부터 0일을 반환한다")
            public void it_returns_zero() {
                RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .when().get("/books/settlement/last")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("passedDays", is(0));
            }
        }
    }

    @Nested
    @DisplayName("getBudget()를 실행할 때")
    class Describe_GetBudget {

        @Nested
        @DisplayName("가계부가 존재하는 경우")
        class Context_With_Exist_Book {
            final User user = UserFixture.emailUser();
            private String token;
            private String bookKey;

            private Map<Month, Double> result;

            @BeforeEach
            public void init() {
                token = UserApiFixture.loginAfterSignup(user).getAccessToken();
                bookKey = BookApiFixture.createBook(token).getBookKey();
                result = new LinkedHashMap<>();
                for (Month month : Month.values()) {
                    result.put(month, 0.0);
                }
            }

            @Test
            @DisplayName("1년의 가계부 예산을 월별로 반환한다")
            public void it_returns_budgets() {
                Map response = RestAssured
                    .given()
                    .auth().oauth2(token)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("bookKey", bookKey)
                    .param("startYear", "2024-01-01")
                    .when().get("/books/budget")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .as(Map.class);

                assertThat(response.toString()).isEqualTo(result.toString());
            }
        }
    }

}

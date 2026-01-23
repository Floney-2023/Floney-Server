package com.floney.floney.acceptance;

import com.floney.floney.acceptance.config.AcceptanceTest;
import com.floney.floney.acceptance.fixture.BookApiFixture;
import com.floney.floney.acceptance.fixture.CategoryApiFixture;
import com.floney.floney.acceptance.fixture.FavoriteApiFixture;
import com.floney.floney.acceptance.fixture.UserApiFixture;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.response.FavoriteResponse;
import com.floney.floney.fixture.UserFixture;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.is;

@AcceptanceTest
@DisplayName("인수 테스트: 카테고리")
public class CategoryAcceptanceTest {

    @Nested
    @DisplayName("카테고리를 생성할 때")
    class Describe_createSubcategory {

        @Nested
        @DisplayName("카테고리 이름이 중복되지 않는 경우")
        class Context_With_NotDuplicatedSubcategoryName {

            final String name = "월급";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("카테고리가 생성된다.")
            void it_returns_subcategory() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted("수입", name))
                    .when()
                    .post("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("name", is(name));
            }

            @Test
            @DisplayName("사용자 정의 카테고리는 categoryKey가 null이다.")
            void custom_category_has_null_categoryKey() {
                // 사용자가 직접 추가한 카테고리 생성
                CategoryApiFixture.createSubcategory(accessToken, bookKey, "수입", name);

                // 조회하여 categoryKey가 null인지 확인
                RestAssured.given()
                    .auth().oauth2(accessToken)
                    .param("parent", "수입")
                    .when()
                    .get("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("find { it.name == '%s' }.categoryKey".formatted(name), is((String) null));
            }
        }

        @Nested
        @DisplayName("카테고리 이름이 중복되는 경우")
        class Context_With_DuplicatedSubcategoryName {

            final String parentName = "수입";
            final String name = "월급";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                CategoryApiFixture.createSubcategory(accessToken, bookKey, parentName, name);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted(parentName, name))
                    .when()
                    .post("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", is("B011"));
            }
        }

        @Nested
        @DisplayName("부모 카테고리가 존재하지 않는 경우")
        class Context_With_NoCategory {

            final String name = "월급";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted("없는 부모 카테고리 이름", name))
                    .when()
                    .post("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", is("B003"));
            }
        }
    }

    @Nested
    @DisplayName("카테고리를 조회할 때")
    class Describe_FindAllSubcategoriesByCategory {

        @Nested
        @DisplayName("해당 부모의 자식 카테고리가 존재하는 경우")
        class Context_With_SubcategoriesByCategory {

            final String parentName = "수입";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("자식 카테고리들을 반환한다.")
            void it_returns_categories() {
                RestAssured.given()
                    .auth().oauth2(accessToken)
                    .param("parent", parentName)
                    .when()
                    .get("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", is(8));
            }

            @Test
            @DisplayName("자식 카테고리들의 categoryKey를 반환한다.")
            void it_returns_categories_with_categoryKey() {
                RestAssured.given()
                    .auth().oauth2(accessToken)
                    .param("parent", parentName)
                    .when()
                    .get("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", is(8))
                    .body("[0].categoryKey", is("SALARY"))
                    .body("[1].categoryKey", is("SIDE_INCOME"))
                    .body("[2].categoryKey", is("ALLOWANCE"));
            }
        }

        @Nested
        @DisplayName("부모 카테고리가 존재하지 않는 경우")
        class Context_With_NoCategory {

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .param("parent", "없는 부모 카테고리 이름")
                    .when()
                    .get("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", is("B003"));
            }
        }
    }

    @Nested
    @DisplayName("카테고리를 삭제할 때")
    class Describe_DeleteSubcategory {

        @Nested()
        @DisplayName("즐겨찾기가 존재하는 경우")
        class Context_With_Favorite {
            final String parentName = "지출";
            final String name = "식비";

            String accessToken;
            String bookKey;

            public void assertFavoritesByCategory(String accessToken, String bookKey, CategoryType categoryType) {
                List<FavoriteResponse> response = FavoriteApiFixture.getFavoriteByCategory(accessToken, bookKey, categoryType);
                Assertions.assertThat(response.size()).isZero();
            }

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                FavoriteApiFixture.createFavorite(accessToken, bookKey);
            }

            @Test
            @DisplayName("즐겨찾기와 카테고리가 모두 삭제된다.")
            void it_returns_empty() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted(parentName, name))
                    .when()
                    .delete("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.OK.value());

                assertFavoritesByCategory(accessToken, bookKey, CategoryType.INCOME);
                assertFavoritesByCategory(accessToken, bookKey, CategoryType.OUTCOME);
                assertFavoritesByCategory(accessToken, bookKey, CategoryType.TRANSFER);
            }
        }

        @Nested
        @DisplayName("해당 카테고리가 존재하는 경우")
        class Context_With_SubcategoryByCategory {

            final String parentName = "수입";
            final String name = "월급";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                CategoryApiFixture.createSubcategory(accessToken, bookKey, parentName, name);
            }

            @Test
            @DisplayName("삭제된다.")
            void it_returns_empty() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted(parentName, name))
                    .when()
                    .delete("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.OK.value());
            }
        }

        @Nested
        @DisplayName("해당 카테고리가 존재하지 않는 경우")
        class Context_With_NoSubcategory {

            final String parentName = "수입";
            final String name = "월급";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted(parentName, name))
                    .when()
                    .delete("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", is("B003"));
            }
        }

        @Nested
        @DisplayName("해당 카테고리가 부모 카테고리에 속하지 않은 경우")
        class Context_With_NoCategory {

            final String parentName = "수입";
            final String name = "월급";

            String accessToken;
            String bookKey;

            @BeforeEach
            void init() {
                accessToken = UserApiFixture.loginAfterSignup(UserFixture.emailUser()).getAccessToken();
                bookKey = BookApiFixture.createBook(accessToken).getBookKey();
                CategoryApiFixture.createSubcategory(accessToken, bookKey, "이체", name);
            }

            @Test
            @DisplayName("에러를 반환한다.")
            void it_returns_error() {
                RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().oauth2(accessToken)
                    .body("""
                        {
                            "parent": "%s",
                            "name": "%s"
                        }
                        """.formatted(parentName, name))
                    .when()
                    .delete("/books/{key}/categories", bookKey)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", is("B003"));
            }
        }
    }
}

package com.floney.floney.acceptance.fixture;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.response.FavoriteResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

public final class FavoriteApiFixture {

    private FavoriteApiFixture() {
    }

    public static int createFavorite(final String accessToken, final String bookKey) {
        return RestAssured.given()
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

    public static int createFavoriteByLineCategory(final String accessToken,
                                                   final String bookKey,
                                                   final String lineCategoryName,
                                                   final String lineSubcategoryName) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(accessToken)
            .body("""
                {
                    "money": 1000,
                    "lineCategoryName": "%s",
                    "lineSubcategoryName": "%s",
                    "assetSubcategoryName": "현금",
                    "exceptStatus": false
                }
                """.formatted(lineCategoryName, lineSubcategoryName))
            .when()
            .post("/books/{key}/favorites", bookKey)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", notNullValue())
            .extract().path("id");
    }

    public static List<FavoriteResponse> getFavoriteByCategory(final String accessToken,
                                                               final String bookKey,
                                                               final CategoryType categoryType) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(accessToken)
            .param("categoryType", categoryType)
            .when()
            .get("/books/{key}/favorites", bookKey)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(List.class);
    }
}

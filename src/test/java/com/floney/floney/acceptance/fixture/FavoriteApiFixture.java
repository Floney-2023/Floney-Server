package com.floney.floney.acceptance.fixture;

import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
}

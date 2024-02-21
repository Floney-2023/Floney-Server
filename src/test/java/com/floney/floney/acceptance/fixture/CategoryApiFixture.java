package com.floney.floney.acceptance.fixture;

import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;

public final class CategoryApiFixture {

    private CategoryApiFixture() {
    }

    public static void createSubcategory(final String accessToken,
                                         final String bookKey,
                                         final String parentName,
                                         final String name) {
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
            .statusCode(HttpStatus.CREATED.value())
            .body("name", is(name));
    }
}

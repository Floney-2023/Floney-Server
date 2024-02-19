package com.floney.floney.acceptance.fixture;

import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class CategoryApiFixture {

    public static CreateCategoryResponse createSubCategory(final String accessToken, final String bookKey, final String lineCategoryName, final String subCategoryName) {
        final CreateCategoryRequest request = CreateCategoryRequest.builder()
            .bookKey(bookKey)
            .parent(lineCategoryName)
            .name(subCategoryName)
            .build();

        return RestAssured
            .given()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when().post("/books/categories")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().as(CreateCategoryResponse.class);
    }
}

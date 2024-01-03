package com.floney.floney.acceptance.fixture;

import com.floney.floney.common.dto.Token;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.dto.request.SignoutRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.entity.User;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserApiFixture {

    public static Token loginAfterSignup(final User user) {
        final SignupRequest request = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .receiveMarketing(user.isReceiveMarketing())
                .build();

        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(Token.class);
    }

    public static void signout(final String accessToken) {
        final SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

        RestAssured
                .given()
                .param("accessToken", accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().delete("/users")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}

package com.floney.floney.acceptance.config;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static io.restassured.filter.log.LogDetail.ALL;

public class IntegrationTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(final TestContext testContext) {
        RestAssured.port = Optional.ofNullable(testContext.getApplicationContext()
                .getEnvironment()
                .getProperty("local.server.port", Integer.class))
            .orElseThrow(() -> new IllegalStateException("localServerPort는 null일 수 없습니다."));

        RestAssured.config = RestAssured.config()
            .logConfig(LogConfig.logConfig()
                .enableLoggingOfRequestAndResponseIfValidationFails(ALL)
                .enablePrettyPrinting(true))
            .encoderConfig(EncoderConfig.encoderConfig()
                .defaultCharsetForContentType(StandardCharsets.UTF_8.name(), ContentType.ANY));
    }
}

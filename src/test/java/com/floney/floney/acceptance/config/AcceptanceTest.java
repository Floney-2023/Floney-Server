package com.floney.floney.acceptance.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Sql(value = "/clear.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
    value = {IntegrationTestExecutionListener.class,},
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public @interface AcceptanceTest {
}

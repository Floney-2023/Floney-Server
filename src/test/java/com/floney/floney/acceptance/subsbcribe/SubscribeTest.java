package com.floney.floney.acceptance.subsbcribe;


import com.floney.floney.subscribe.dto.GetAppleTransactionResponse;
import com.floney.floney.user.client.AppleClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
@Transactional
public class SubscribeTest {

    private final AppleClient appleClient;

    @Autowired
    public SubscribeTest(final AppleClient appleClient) {
        this.appleClient = appleClient;
    }

    @Nested
    @DisplayName("구독 유효성을 검증할 때")
    class DescribeRequestAppleTransaction {
        @Nested
        @DisplayName("트랜잭션 아이디가 유효하지 않다면")
        class ContextWithInvalidTransactionId {
            @Test
            @DisplayName("valid가 false이다")
            void it_return_code() throws IOException {
                GetAppleTransactionResponse response = appleClient.getTransaction("1234");
                Assertions.assertThat(response.isValid).isFalse();
            }
        }
    }
}
package com.floney.floney.user.service;

import com.floney.floney.common.exception.user.PasswordSameException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Disabled("사용하지 않는 객체")
@DisplayName("PasswordHistoryManager 테스트")
@SpringBootTest
class PasswordHistoryManagerTest {

    private static final String KEY_USER = "user:";
    private static final String KEY_PASSWORDS = ":passwords";

    @Autowired
    private PasswordHistoryManager passwordHistoryManager;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOperations;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Nested
    @DisplayName("addPassword 메서드에서")
    class Describe_AddPassword {

        @Nested
        @DisplayName("이전 비밀번호 내역에 현재 비밀번호가 포함되지 않는 경우")
        class Context_PasswordNotIncludedInHistory {

            final String password = "new";
            final long userId = 1;
            final String key = KEY_USER + userId + KEY_PASSWORDS;

            @BeforeEach
            public void init() {
                passwordHistoryManager.addPassword("a", userId);
                passwordHistoryManager.addPassword("b", userId);
                passwordHistoryManager.addPassword("c", userId);
            }

            @AfterEach
            public void reset() {
                redisTemplate.delete(key);
            }

            @Test
            @DisplayName("현재 비밀번호를 내역에 추가한다.")
            void it_succeeds() {
                passwordHistoryManager.addPassword(password, userId);

                final String encodedPassword = listOperations.rightPop(key);
                assertThat(passwordEncoder.matches(password, encodedPassword)).isTrue();
            }
        }

        @Nested
        @DisplayName("이전 비밀번호 내역이 비어있는 경우")
        class Context_EmptyPasswordHistory {

            final String password = "new";
            final long userId = 1;
            final String key = KEY_USER + userId + KEY_PASSWORDS;


            @AfterEach
            public void reset() {
                redisTemplate.delete(key);
            }

            @Test
            @DisplayName("현재 비밀번호를 내역에 추가한다.")
            void it_succeeds() {
                passwordHistoryManager.addPassword(password, userId);

                final String encodedPassword = listOperations.rightPop(key);
                assertThat(passwordEncoder.matches(password, encodedPassword)).isTrue();
            }
        }

        @Nested
        @DisplayName("이전 비밀번호 내역에 현재 비밀번호가 포함되는 경우")
        class Context_PasswordIncludedInHistory {

            final String password = "new";
            final long userId = 1;
            final String key = KEY_USER + userId + KEY_PASSWORDS;

            @BeforeEach
            public void init() {
                passwordHistoryManager.addPassword(password, userId);
            }

            @AfterEach
            public void reset() {
                redisTemplate.delete(key);
            }

            @Test
            @DisplayName("에러가 발생한다.")
            void it_returns_error() {
                assertThatThrownBy(() -> passwordHistoryManager.addPassword(password, userId))
                    .isInstanceOf(PasswordSameException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteHistory 메서드에서")
    class Describe_DeleteHistory {

        @Nested
        @DisplayName("회원이 비밀번호 내역을 가지고 있는 경우")
        class Context_UserHasPasswordHistory {

            final long userId = 1;
            final String key = KEY_USER + userId + KEY_PASSWORDS;

            @BeforeEach
            public void init() {
                passwordHistoryManager.addPassword("a", userId);
                passwordHistoryManager.addPassword("b", userId);
                passwordHistoryManager.addPassword("c", userId);
            }

            @Test
            @DisplayName("비밀번호 내역을 지운다.")
            void it_succeeds() {
                passwordHistoryManager.deleteHistory(userId);

                assertThat(redisTemplate.hasKey(key)).isFalse();
            }
        }
    }
}

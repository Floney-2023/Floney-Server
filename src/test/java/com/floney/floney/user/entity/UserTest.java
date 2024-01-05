package com.floney.floney.user.entity;

import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.constant.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User 테스트")
class UserTest {

    @Nested
    @DisplayName("signupByEmail 메서드에서")
    class Describe_SignupByEmail {

        @Nested
        @DisplayName("email, password, nickname, receiveMarketing 이 조건을 만족하는 경우")
        class Context_ValidParameters {

            final String email = "test@email.com";
            final String password = "password";
            final String nickname = "nickname";
            final boolean receiveMarketing = true;

            @Test
            @DisplayName("User를 생성한다.")
            void it_returns_user() {
                final User user = User.signupByEmail(email, password, nickname, receiveMarketing);

                assertThat(user)
                    .hasFieldOrPropertyWithValue("email", email)
                    .hasFieldOrPropertyWithValue("nickname", nickname)
                    .hasFieldOrPropertyWithValue("password", password)
                    .hasFieldOrPropertyWithValue("provider", Provider.EMAIL)
                    .hasFieldOrPropertyWithValue("receiveMarketing", receiveMarketing);
            }
        }

        @Nested
        @DisplayName("email이 비어있는 경우")
        class Context_BlankEmail {

            final String email = " ";

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail(email, "password", "nickname", true))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("email의 길이가 350자를 넘는 경우")
        class Context_TooLongEmail {

            final String email = "a".repeat(351);

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail(email, "password", "nickname", true))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("password가 비어있는 경우")
        class Context_BlankPassword {

            final String password = " ";

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail("test@email.com", password, "nickname", true))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("password의 길이가 8자를 넘지 않는 경우")
        class Context_TooShortPassword {

            final String password = "a".repeat(7);

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail("test@email.com", password, "nickname", true))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("password의 길이가 32자를 넘는 경우")
        class Context_TooLongPassword {

            final String password = "a".repeat(33);

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail("test@email.com", password, "nickname", true))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("nickname이 비어있는 경우")
        class Context_BlankNickname {

            final String nickname = " ";

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail("test@email.com", "password", nickname, true))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("nickname의 길이가 8자를 넘는 경우")
        class Context_TooLongNickname {

            final String nickname = "a".repeat(9);

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> User.signupByEmail("test@email.com", "password", nickname, true))
                    .isInstanceOf(RuntimeException.class);
            }
        }
    }

    @Nested
    @DisplayName("updatePassword 메서드에서")
    class Describe_UpdatePassword {

        @Nested
        @DisplayName("새 password가 올바른 경우")
        class Context_ValidPassword {

            final User user = UserFixture.emailUser();
            final String password = "newPassword";

            @Test
            @DisplayName("password를 변경한다.")
            void it_changes_password() {
                user.updatePassword(password);

                assertThat(user.getPassword()).isEqualTo(password);
            }
        }

        @Nested
        @DisplayName("새 password가 비어있는 경우")
        class Context_BlankPassword {

            final User user = UserFixture.emailUser();
            final String password = " ";

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> user.updatePassword(password))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("새 password의 길이가 8자를 넘지 않는 경우")
        class Context_TooShortPassword {

            final User user = UserFixture.emailUser();
            final String password = "a".repeat(7);

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> user.updatePassword(password))
                    .isInstanceOf(RuntimeException.class);
            }
        }

        @Nested
        @DisplayName("새 password의 길이가 32자를 넘는 경우")
        class Context_TooLongPassword {

            final User user = UserFixture.emailUser();
            final String password = "a".repeat(33);

            @Test
            @DisplayName("예외가 발생한다.")
            void it_throws_exception() {
                assertThatThrownBy(() -> user.updatePassword(password))
                    .isInstanceOf(RuntimeException.class);
            }
        }
    }
}

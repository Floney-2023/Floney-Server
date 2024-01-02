package com.floney.floney.user.entity;

import com.floney.floney.user.dto.constant.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User 테스트")
class UserTest {

    @Nested
    @DisplayName("provider가 email인 User를 생성할 때")
    class signupByEmailTest {

        @Test
        @DisplayName("성공한다.")
        void success() {
            /* given */
            final String email = "test@email.com";
            final String password = "password";
            final String nickname = "nickname";
            final boolean receiveMarketing = true;

            /* when */
            final User user = User.signupByEmail(email, password, nickname, receiveMarketing);

            /* then */
            assertThat(user)
                    .hasFieldOrPropertyWithValue("email", email)
                    .hasFieldOrPropertyWithValue("nickname", nickname)
                    .hasFieldOrPropertyWithValue("password", password)
                    .hasFieldOrPropertyWithValue("provider", Provider.EMAIL)
                    .hasFieldOrPropertyWithValue("receiveMarketing", receiveMarketing);
        }

        @Test
        @DisplayName("email이 비어있어 실패한다.")
        void fail_blankEmail() {
            /* given */
            final String email = " ";

            /* when & then */
            assertThatThrownBy(() -> User.signupByEmail(email, "password", "nickname", true))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("email의 길이가 350자를 넘어 실패한다.")
        void fail_tooLongEmail() {
            /* given */
            final String email = "a".repeat(351);

            /* when & then */
            assertThatThrownBy(() -> User.signupByEmail(email, "password", "nickname", true))
                    .isInstanceOf(RuntimeException.class);
        }
    }
}

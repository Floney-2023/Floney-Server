package com.floney.floney.user.service;

import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.user.NotEmailUserException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.response.ReceiveMarketingResponse;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SignoutReasonRepository;
import com.floney.floney.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookUserRepository bookUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SignoutReasonRepository signoutReasonRepository;
    @Mock
    private MailProvider mailProvider;
    @Mock
    private PasswordHistoryManager passwordHistoryManager;

    @Test
    @DisplayName("마케팅 수신 동의 여부를 변경하는데 성공한다")
    void updateReceiveMarketing_success() {
        // given
        User user = UserFixture.emailUser();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatNoException()
            .isThrownBy(() -> userService.updateReceiveMarketing(false, user.getEmail()));
        assertThat(user.isReceiveMarketing()).isFalse();
    }

    @Test
    @DisplayName("마케팅 수신 동의 여부를 변경하는데 실패한다 - 존재하지 않는 유저")
    void updateReceiveMarketing_fail_userNotFoundException() {
        // given
        final String email = "notExist@email.com";
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateReceiveMarketing(false, email))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호 재발급 및 변경에 성공한다")
    void updateRegeneratedPassword_success() {
        // given
        final User user = UserFixture.emailUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatNoException().isThrownBy(() -> userService.updatePasswordByGeneration(user.getEmail()));
    }

    @Test
    @DisplayName("비밀번호 재발급 및 변경에 실패한다 - 존재하지 않는 회원")
    void updateRegeneratedPassword_fail_throws_userNotFoundException() {
        // when & then
        assertThatThrownBy(() -> userService.updatePasswordByGeneration("notUser"))
            .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("비밀번호 재발급 및 변경에 실패한다 - 간편 회원")
    void updateRegeneratedPassword_fail_throws_notEmailUserException() {
        // given
        final String email = "notEmailUser";
        final User notEmailUser = UserFixture.kakaoUser();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(notEmailUser));

        // when & then
        assertThatThrownBy(() -> userService.updatePasswordByGeneration(email))
            .isInstanceOf(NotEmailUserException.class);
    }

    @Test
    @DisplayName("마케팅 수신 동의 여부를 조회하는데 성공한다")
    void getReceiveMarketing_success() {
        // given
        User user = UserFixture.emailUser();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when
        final ReceiveMarketingResponse response = userService.getReceiveMarketing(user.getEmail());

        // then
        assertThat(response.isReceiveMarketing()).isEqualTo(user.isReceiveMarketing());
    }

    @Test
    @DisplayName("마케팅 수신 동의 여부를 조회하는데 실패한다 - 존재하지 않는 유저")
    void getReceiveMarketing_fail_userNotFoundException() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.getReceiveMarketing("fail@email.com"))
            .isInstanceOf(UserNotFoundException.class);
    }
}

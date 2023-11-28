package com.floney.floney.user.service;

import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.user.*;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.dto.request.SignoutRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.response.MyPageResponse;
import com.floney.floney.user.dto.response.ReceiveMarketingResponse;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.SignoutReasonRepository;
import com.floney.floney.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static com.floney.floney.common.constant.Status.INACTIVE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookUserRepository bookUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SignoutReasonRepository signoutReasonRepository;
    @Mock
    private MailProvider mailProvider;

    @Test
    @DisplayName("회원가입에 성공한다")
    void signup_success() {
        // given
        User user = UserFixture.getUser();
        SignupRequest signupRequest = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .receiveMarketing(user.isReceiveMarketing())
                .build();

        given(userRepository.save(any(User.class))).willReturn(null);

        // when
        userService.signup(signupRequest);

        // then
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입에 실패한다 - 이미 가입된 회원")
    void signup_fail_throws_userFoundException1() {
        // given
        User user = UserFixture.getUser();
        SignupRequest signupRequest = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .receiveMarketing(user.isReceiveMarketing())
                .build();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.signup(signupRequest)).isInstanceOf(UserFoundException.class);
    }

    @Test
    @DisplayName("회원탈퇴에 성공한다")
    void signout_success() {
        // given
        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", 1L);
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

        // when
        userService.signout(user.getEmail(), request);

        // then
        assertThat(user.getStatus()).isEqualTo(INACTIVE);
        assertThat(user.getEmail()).isEqualTo(UserFixture.DELETE_VALUE);
        assertThat(user.getPassword()).isEqualTo(UserFixture.DELETE_VALUE);
        assertThat(user.getNickname()).isEqualTo(UserFixture.DELETE_VALUE);
        assertThat(user.getProfileImg()).isNull();
        assertThat(user.getProviderId()).isNull();
    }

    @Test
    @DisplayName("회원탈퇴에 실패한다 - 존재하지 않는 회원")
    void signout_fail_throws_usernameNotFoundException() {
        // given
        final String email = "email";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        SignoutRequest request = new SignoutRequest(SignoutType.EXPENSIVE, null);

        // when & then
        assertThatThrownBy(() -> userService.signout(email, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("회원탈퇴에 실패한다 - 비어있는 기타 탈퇴 사유")
    void signout_fail_throws_signoutOtherReasonEmptyException(final String value) {
        // given
        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", 1L);
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        SignoutRequest request = new SignoutRequest(SignoutType.OTHER, value);

        // when & then
        assertThatThrownBy(() -> userService.signout(user.getEmail(), request))
                .isInstanceOf(SignoutOtherReasonEmptyException.class);
    }

    @Test
    @DisplayName("회원정보 얻기에 성공한다")
    void getUserInfo_success() {
        // given
        User user = UserFixture.getUser();
        given(bookUserRepository.findMyBookInfos(user)).willReturn(Collections.singletonList(BookFixture.myBookInfo()));

        // when & then
        assertThat(userService.getUserInfo(CustomUserDetails.of(user)))
                .isEqualTo(MyPageResponse.from(UserResponse.from(user), Collections.singletonList(BookFixture.myBookInfo())));
    }

    @Test
    @DisplayName("현재 비밀번호와 같은 비밀번호로 변경 시도 시 실패한다")
    void updatePassword_fail_samePasswordException() {
        // given
        final String password = "1234567890";
        final User user = User.builder().email("email@email.com").password(password).build();

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(password, user.getEmail()))
                .isInstanceOf(PasswordSameException.class);
    }

    @Test
    @DisplayName("마케팅 수신 동의 여부를 변경하는데 성공한다")
    void updateReceiveMarketing_success() {
        // given
        User user = UserFixture.createUser();
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
        final User user = UserFixture.createUser();

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatNoException().isThrownBy(() -> userService.updateRegeneratedPassword(user.getEmail()));
    }

    @Test
    @DisplayName("비밀번호 재발급 및 변경에 실패한다 - 존재하지 않는 회원")
    void updateRegeneratedPassword_fail_throws_userNotFoundException() {
        // when & then
        assertThatThrownBy(() -> userService.updateRegeneratedPassword("notUser"))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("비밀번호 재발급 및 변경에 실패한다 - 간편 회원")
    void updateRegeneratedPassword_fail_throws_notEmailUserException() {
        // given
        final String email = "notEmailUser";
        final User notEmailUser = UserFixture.createKakaoUser();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(notEmailUser));

        // when & then
        assertThatThrownBy(() -> userService.updateRegeneratedPassword(email))
                .isInstanceOf(NotEmailUserException.class);
    }

    @Test
    @DisplayName("마케팅 수신 동의 여부를 조회하는데 성공한다")
    void getReceiveMarketing_success() {
        // given
        User user = UserFixture.createUser();
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

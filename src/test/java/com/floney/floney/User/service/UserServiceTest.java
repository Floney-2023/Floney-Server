package com.floney.floney.User.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.floney.floney.common.exception.UserFoundException;
import com.floney.floney.common.exception.UserSignoutException;
import com.floney.floney.common.token.JwtTokenProvider;
import com.floney.floney.common.token.RedisProvider;
import com.floney.floney.config.UserFixture;
import com.floney.floney.user.dto.MyPageResponse;
import com.floney.floney.user.dto.UserResponse;
import com.floney.floney.user.dto.request.UserSignupRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import com.floney.floney.user.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RedisProvider redisProvider;
    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("회원가입에 성공한다")
    void signup_success() {
        // given
        User user = UserFixture.getUser();
        UserSignupRequest userSignupRequest = UserSignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .marketingAgree(user.isMarketingAgree())
                .build();

        given(userRepository.save(any(User.class))).willReturn(null);

        // when
        userService.signup(userSignupRequest);

        // then
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입에 실패한다 - 이미 가입된 회원")
    void signup_fail_throws_userFoundException() {
        // given
        User user = UserFixture.getUser();
        UserSignupRequest userSignupRequest = UserSignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .marketingAgree(user.isMarketingAgree())
                .build();

        // when & then
        assertThatThrownBy(() -> userService.signup(userSignupRequest))
                .isInstanceOf(UserFoundException.class);
    }

    @Test
    @DisplayName("회원탈퇴에 성공한다")
    void signout_success() {
        // given
        User user = UserFixture.createUser();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when
        userService.signout(user.getEmail());

        // then
        assertThat(user.isStatus()).isFalse();
    }

    @Test
    @DisplayName("회원탈퇴에 실패한다 - 이미 탈퇴한 회원")
    void signout_fail_throws_userSignoutException() {
        // given
        User user = UserFixture.createUser();
        user.signout();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.signout(user.getEmail())).isInstanceOf(UserSignoutException.class);
    }

    @Test
    @DisplayName("회원탈퇴에 실패한다 - 존재하지 않는 회원")
    void signout_fail_throws_usernameNotFoundException() {
        // given
        User user = UserFixture.createUser();
        given(userRepository.findByEmail(user.getEmail())).willThrow(UsernameNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> userService.signout(user.getEmail())).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("회원정보 얻기에 성공한다")
    void getUserInfo_success() {
        // given
        User user = UserFixture.getUser();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThat(userService.getUserInfo(user.getEmail())).isEqualTo(MyPageResponse.from(UserResponse.from(user)));
    }

    @Test
    @DisplayName("회원정보 얻기에 실패한다 - 존재하지 않는 회원")
    void getUserInfo_fail_throws_userNotFoundException() {
        // given
        User user = UserFixture.getUser();
        given(userRepository.findByEmail(user.getEmail())).willThrow(UsernameNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> userService.getUserInfo(user.getEmail())).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("이메일 인증코드를 올바르게 생성하는 데 성공한다")
    void generateEmailAuthenticationCode_success() {
        // given
        int codeLength = 6;

        // when
        String code = userService.sendAuthenticateEmail("email");

        // then
        assertThat(code.length()).isEqualTo(codeLength);
    }

    @Test
    @DisplayName("새 비밀번호를 올바르게 생성하는 데 성공한다")
    void generateNewPassword_success() {
        // given
        int passwordLength = 50;

        // when
        String newPassword = userService.sendPasswordFindEmail("email");

        // then
        assertThat(newPassword.length()).isEqualTo(passwordLength);
    }

}
package com.floney.floney.user.service;

import com.floney.floney.common.dto.Token;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.common.util.JwtProvider;
import com.floney.floney.common.util.MailProvider;
import com.floney.floney.common.util.RedisProvider;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailProvider mailProvider;
    @Mock
    private RedisProvider redisProvider;
    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("로그인에 성공한다")
    void login_success() {
        // given
        User user = UserFixture.createUser();

        LoginRequest request = LoginRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtProvider.generateToken(anyString())).willReturn(new Token("accessToken", "refreshToken"));

        // when & then
        assertThatNoException().isThrownBy(() -> authenticationService.login(request));
        assertThat(user.isInactive()).isFalse();
        assertThat(user.getLastLoginTime()).isNotNull();
    }

    @Test
    @DisplayName("로그인에 실패한다 - 존재하지 않는 회원")
    void login_fail_throws_userNotFoundException() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("fail@fail.com")
                .password("fail")
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationService.login(request)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("로그인에 실패한다 - 일치하지 않는 비밀번호")
    void login_fail_throws_badCredentialException() {
        // given
        User user = UserFixture.getUser();

        LoginRequest request = LoginRequest.builder()
                .email(user.getEmail())
                .password("fail")
                .build();

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationService.login(request)).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("이메일 인증코드를 올바르게 생성하는 데 성공한다")
    void generateEmailAuthenticationCode_success() {
        // given
        int codeLength = 6;

        // when
        String code = authenticationService.sendEmailAuthMail("email");

        // then
        assertThat(code.length()).isEqualTo(codeLength);
    }
}

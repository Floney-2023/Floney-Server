package com.floney.floney.User.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.UserFoundException;
import com.floney.floney.common.exception.UserSignoutException;
import com.floney.floney.config.UserFixture;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import com.floney.floney.user.service.CustomUserDetailsService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입에 실패한다 - 이미 가입된 회원")
    void signup_fail_throws_userFoundException() {
        // given
        User user = UserFixture.getUser();
        user.delete();
        SignupRequest signupRequest = SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .marketingAgree(user.getMarketingAgree())
                .build();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> customUserDetailsService.validateIfNewUser(signupRequest.getEmail()))
                .isInstanceOf(UserSignoutException.class);
    }
}
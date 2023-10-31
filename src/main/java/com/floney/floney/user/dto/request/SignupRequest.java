package com.floney.floney.user.dto.request;

import com.floney.floney.user.dto.constant.Provider;
import com.floney.floney.user.entity.User;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupRequest {

    @NotNull(message = "이메일을 입력해주세요")
    private String email;

    @NotNull(message = "닉네임을 입력해주세요")
    private String nickname;

    @NotNull(message = "비밀번호를 입력해주세요")
    private String password;

    @NotNull(message = "마케팅 수신 동의 여부를 입력해주세요")
    private Boolean receiveMarketing;

    public LoginRequest toLoginRequest() {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public User to() {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.EMAIL)
                .receiveMarketing(receiveMarketing)
                .build();
    }

    public User to(Provider provider, String providerId) {
        return User.builder()
                .email(email)
                .password("auth")
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .receiveMarketing(receiveMarketing)
                .build();
    }

    @Override
    public String toString() {
        return "SignupRequest{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}

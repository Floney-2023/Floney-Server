package com.floney.floney.user.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotNull(message = "새 비밀번호를 입력해주세요")
    @NotBlank(message = "새 비밀번호를 입력해주세요")
    private String newPassword;

    @NotNull(message = "기존 비밀번호를 입력해주세요")
    @NotBlank(message = "기존 비밀번호를 입력해주세요")
    private String oldPassword;
}

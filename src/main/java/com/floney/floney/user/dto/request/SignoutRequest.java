package com.floney.floney.user.dto.request;

import com.floney.floney.common.exception.user.SignoutOtherReasonEmptyException;
import com.floney.floney.user.dto.constant.SignoutType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SignoutRequest {

    @NotNull(message = "회원 탈퇴 사유를 입력해주세요")
    private SignoutType type;

    private String reason;

    public void validateReasonNotEmpty() {
        if (reason == null || reason.isBlank()) {
            throw new SignoutOtherReasonEmptyException();
        }
    }
}

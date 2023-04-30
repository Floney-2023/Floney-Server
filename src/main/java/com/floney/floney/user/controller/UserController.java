package com.floney.floney.user.controller;

import com.floney.floney.common.BaseException;
import com.floney.floney.common.BaseResponse;
import com.floney.floney.common.BaseResponseStatus;
import com.floney.floney.common.jwt.dto.TokenDto;
import com.floney.floney.user.dto.request.UserLoginRequestDto;
import com.floney.floney.user.dto.request.UserSignupRequestDto;
import com.floney.floney.user.dto.security.UserDetail;
import com.floney.floney.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public BaseResponse<?> signup(@RequestBody @Validated UserSignupRequestDto signupRequestDto) {
        try {
            userService.signup(signupRequestDto.to());
            userService.login(signupRequestDto.getEmail(), signupRequestDto.getPassword());
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/email")
    public BaseResponse<?> authenticateEmail(@RequestBody @Validated String email) {
        try {
            return new BaseResponse<>(userService.authenticateEmail(email));
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.AUTHENTICATION_FAIL);
        }
    }

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody @Validated UserLoginRequestDto loginRequestDto) {
        try{
            String email = loginRequestDto.getEmail();
            String password = loginRequestDto.getPassword();
            return new BaseResponse<>(userService.login(email, password));
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.AUTHENTICATION_FAIL);
        }

    }

    @PostMapping("/logout")
    public BaseResponse<?> logout(@RequestBody @Validated String accessToken) {
        try {
            userService.logout(accessToken);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/reissue")
    public BaseResponse<?> regenerateToken(@RequestBody @Validated TokenDto tokenDto) {
        try {
            return new BaseResponse<>(userService.regenerateToken(tokenDto));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/signout")
    public BaseResponse<?> signout(@RequestBody @Validated String accessToken) {
        try {
            String email = userService.logout(accessToken);
            userService.signout(email);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/nickname")
    public BaseResponse<?> updateNickname(@RequestBody @Validated String nickname) {
        try {
            userService.updateNickname(nickname);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/password")
    public BaseResponse<?> updatePassword(@RequestBody @Validated String password) {
        try {
            userService.updatePassword(password);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}

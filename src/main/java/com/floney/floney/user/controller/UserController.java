package com.floney.floney.user.controller;

import com.floney.floney.common.token.dto.TokenDto;
import com.floney.floney.user.dto.request.UserLoginRequest;
import com.floney.floney.user.dto.request.UserSignupRequest;
import com.floney.floney.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<?> signup(@RequestBody @Validated UserSignupRequest signupRequestDto) {
        userService.signup(signupRequestDto.to());
        return new ResponseEntity<>(
                userService.login(signupRequestDto.getEmail(), signupRequestDto.getPassword()),
                HttpStatus.CREATED);
    }

    @PostMapping("/email")
    public ResponseEntity<?> authenticateEmail(@RequestBody @Validated String email) {
        return new ResponseEntity<>(
                userService.sendAuthenticateEmail(email),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserLoginRequest loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        return new ResponseEntity<>(userService.login(email, password), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Validated String accessToken) {
        userService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> regenerateToken(@RequestBody @Validated TokenDto tokenDto) {
        return new ResponseEntity<>(
                userService.regenerateToken(tokenDto),
                HttpStatus.CREATED);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestBody @Validated String accessToken) {
        String email = userService.logout(accessToken);
        userService.signout(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody @Validated String nickname) {
        userService.updateNickname(nickname);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Validated String password) {
        userService.updatePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody @Validated String email) {
        userService.updatePassword(email, userService.sendPasswordFindEmail(email));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/img")
    public ResponseEntity<?> updateProfileImg(@RequestBody @Validated String profileImg) {
        userService.updateProfileImg(profileImg);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal @Validated UserDetails principal) {
        return new ResponseEntity<>(userService.getUserInfo(principal.getUsername()), HttpStatus.OK);
    }

}

package com.floney.floney.user.controller;

import com.floney.floney.common.token.dto.TokenDto;
import com.floney.floney.user.dto.request.UserLoginRequest;
import com.floney.floney.user.dto.request.UserSignupRequest;
import com.floney.floney.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> signup(@RequestBody @Valid UserSignupRequest userSignupRequest) {
        userService.signup(userSignupRequest);
        return new ResponseEntity<>(userService.login(userSignupRequest.toLoginRequest()), HttpStatus.CREATED);
    }

    @PostMapping("/email")
    public ResponseEntity<?> authenticateEmail(@RequestBody @Valid String email) {
        return new ResponseEntity<>(userService.sendAuthenticateEmail(email), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        return new ResponseEntity<>(userService.login(userLoginRequest), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid String accessToken) {
        userService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> regenerateToken(@RequestBody @Valid TokenDto tokenDto) {
        return new ResponseEntity<>(userService.regenerateToken(tokenDto), HttpStatus.CREATED);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestBody String accessToken) {
        String email = userService.logout(accessToken);
        userService.signout(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody String nickname) {
        userService.updateNickname(nickname);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody String password) {
        userService.updatePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody String email) {
        userService.updatePassword(email, userService.sendPasswordFindEmail(email));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/img")
    public ResponseEntity<?> updateProfileImg(@RequestBody String profileImg) {
        userService.updateProfileImg(profileImg);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetails principal) {
        return new ResponseEntity<>(userService.getUserInfo(principal.getUsername()), HttpStatus.OK);
    }

}

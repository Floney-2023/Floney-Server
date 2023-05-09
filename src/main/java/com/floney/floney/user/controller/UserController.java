package com.floney.floney.user.controller;

import com.floney.floney.common.token.dto.Token;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/email")
    public ResponseEntity<?> authenticateEmail(@RequestParam String email) {
        return new ResponseEntity<>(userService.sendAuthenticateEmail(email), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        return new ResponseEntity<>(userService.login(userLoginRequest), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String accessToken) {
        userService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestBody @Valid Token token) {
        return new ResponseEntity<>(userService.reissueToken(token), HttpStatus.CREATED);
    }

    @GetMapping("/signout")
    public ResponseEntity<?> signout(@RequestParam String accessToken) {
        userService.signout(userService.logout(accessToken));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> updateNickname(@RequestParam String nickname) {
        userService.updateNickname(nickname);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestParam String password) {
        userService.updatePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestParam String email) {
        userService.updatePassword(email, userService.sendPasswordFindEmail(email));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/img")
    public ResponseEntity<?> updateProfileImg(@RequestParam String profileImg) {
        userService.updateProfileImg(profileImg);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetails userDetail) {
        return new ResponseEntity<>(userService.getUserInfo(userDetail.getUsername()), HttpStatus.OK);
    }

}

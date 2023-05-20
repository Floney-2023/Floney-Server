package com.floney.floney.user.controller;

import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.service.OAuthUserService;
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
    private final OAuthUserService oAuthUserService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest signupRequest) {
        userService.validateIfNewUser(signupRequest.getEmail());
        return new ResponseEntity<>(userService.login(userService.signup(signupRequest)), HttpStatus.CREATED);
    }

    @GetMapping("/email/mail")
    public ResponseEntity<?> sendEmailAuthMail(@RequestParam String email) {
        userService.sendEmailAuthMail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/email/mail")
    public ResponseEntity<?> authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest emailAuthenticationRequest) {
        userService.authenticateEmail(emailAuthenticationRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        return new ResponseEntity<>(userService.login(loginRequest), HttpStatus.OK);
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

    @GetMapping("/nickname/update")
    public ResponseEntity<?> updateNickname(@RequestParam String nickname, @AuthenticationPrincipal UserDetails userDetail) {
        userService.updateNickname(nickname, userDetail.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestParam String password, @AuthenticationPrincipal UserDetails userDetail) {
        userService.updatePassword(userDetail.getUsername(), password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/password/find")
    public ResponseEntity<?> findPassword(@RequestParam String email) {
        userService.updatePassword(email, userService.sendPasswordFindEmail(email));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/profileimg/update")
    public ResponseEntity<?> updateProfileImg(@RequestParam String profileImg, @AuthenticationPrincipal UserDetails userDetail) {
        userService.updateProfileImg(profileImg, userDetail.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetails userDetail) {
        return new ResponseEntity<>(userService.getUserInfo(userDetail.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<?> validateIfNewUser(@RequestParam String email) {
        userService.validateIfNewUser(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam String oAuthToken) {
        return new ResponseEntity<>(oAuthUserService.kakaoLogin(oAuthToken), HttpStatus.OK);
    }

}

package com.floney.floney.user.controller;

import com.floney.floney.common.token.dto.Token;
import com.floney.floney.user.dto.request.EmailAuthenticationRequest;
import com.floney.floney.user.dto.request.LoginRequest;
import com.floney.floney.user.dto.request.SignupRequest;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.service.CustomUserDetailsService;
import com.floney.floney.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request) {
        customUserDetailsService.validateIfNewUser(request.getEmail());
        return new ResponseEntity<>(userService.login(userService.signup(request)), HttpStatus.CREATED);
    }

    @GetMapping("/email/mail")
    public ResponseEntity<?> sendEmailAuthMail(@RequestParam String email) {
        userService.sendEmailAuthMail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/email/mail")
    public ResponseEntity<?> authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request) {
        userService.authenticateEmail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
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
    public ResponseEntity<?> updateNickname(@RequestParam String nickname, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateNickname(nickname, userDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestParam String password, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePassword(password, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/password/find")
    public ResponseEntity<?> findPassword(@RequestParam String email) {
        String password = userService.sendPasswordFindEmail(email);
        userService.updatePassword(password, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/profileimg/update")
    public ResponseEntity<?> updateProfileImg(@RequestParam String profileImg, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateProfileImg(profileImg, userDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getUserInfo(userDetails), HttpStatus.OK);
    }

}

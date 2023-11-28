package com.floney.floney.user.controller;

import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.common.dto.Token;
import com.floney.floney.user.dto.request.*;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.service.AuthenticationService;
import com.floney.floney.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * 회원가입
     *
     * @param request 회원 가입 요청
     * @return 회원가입한 유저의 access token
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Token signup(@RequestBody @Valid SignupRequest request) {
        return authenticationService.login(userService.signup(request));
    }

    /**
     * 이메일 인증 메일 전송
     *
     * @param email 이메일 주소
     */
    @GetMapping("/email/mail")
    public ResponseEntity<?> sendEmailAuthMail(@RequestParam String email) {
        authenticationService.sendEmailAuthMail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 이메일 인증 메일의 코드를 검사
     *
     * @param request 이메일 주소와 인증 코드
     */
    @PostMapping("/email/mail")
    public ResponseEntity<?> authenticateEmail(@RequestBody @Valid EmailAuthenticationRequest request) {
        authenticationService.authenticateEmail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 로그인
     *
     * @param request 로그인 요청
     * @return 로그인 한 유저의 access token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(authenticationService.login(request), HttpStatus.OK);
    }

    /**
     * 로그아웃
     *
     * @param accessToken 로그아웃 할 유저의 access token
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String accessToken) {
        authenticationService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * token 재발급
     *
     * @param token 기존 토큰
     * @return 새로운 토큰
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestBody @Valid Token token) {
        return new ResponseEntity<>(authenticationService.reissueToken(token), HttpStatus.CREATED);
    }

    /**
     * 회원 탈퇴
     *
     * @param accessToken 탈퇴할 유저의 access token
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void signout(@RequestParam String accessToken,
                        @RequestBody @Valid final SignoutRequest request) {
        userService.signout(authenticationService.logout(accessToken), request);
    }

    /**
     * 회원 비밀번호와 요청 비밀번호가 일치하는 지 검사
     * @param userDetails
     * @param request
     */
    @GetMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void authenticatePassword(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                     @RequestBody @Valid final PasswordAuthenticateRequest request) {
        authenticationService.authenticatePassword(userDetails.getUsername(), request);
    }

    /**
     * 회원 닉네임 수정
     *
     * @param nickname    수정할 닉네임
     * @param userDetails 접속한 유저 정보
     */
    @GetMapping("/nickname/update")
    public ResponseEntity<?> updateNickname(@RequestParam String nickname, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateNickname(nickname, userDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 회원 비밀번호 수정
     *
     * @param request     수정할 비밀번호
     * @param userDetails 접속한 유저 정보
     */
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@RequestBody final UpdatePasswordRequest request,
                               @AuthenticationPrincipal final CustomUserDetails userDetails) {
        userService.updatePassword(request.getNewPassword(), userDetails.getUsername());
    }

    /**
     * 이메일 회원 비밀번호 재발급
     *
     * @param email 회원 이메일 주소
     */
    @GetMapping("/password/find")
    @ResponseStatus(HttpStatus.OK)
    public void regeneratePassword(@RequestParam final String email) {
        userService.updateRegeneratedPassword(email);
    }

    /**
     * 회원 프로필 이미지 수정
     *
     * @param profileImg  새 프로필 이미지 주소
     * @param userDetails 접속한 유저 정보
     */
    @GetMapping("/profileimg/update")
    public ResponseEntity<?> updateProfileImg(@RequestParam String profileImg, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateProfileImg(profileImg, userDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 마이페이지 조회
     *
     * @param userDetails 접속한 유저 정보
     * @return 마이페이지 정보
     */
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getUserInfo(userDetails), HttpStatus.OK);
    }

    /**
     * 최근 접근 가계부 키 저장
     *
     * @body SaveRecentBookKeyRequest 가계부 키
     */

    @PostMapping("/bookKey")
    public ResponseEntity<?> recentBookKey(@RequestBody SaveRecentBookKeyRequest request,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.saveRecentBookKey(request, customUserDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 회원 마케팅 수신 동의 여부 변경
     *
     * @param receiveMarketing  마케팅 수신 동의 여부
     * @param customUserDetails 회원 정보
     */
    @PutMapping("/receive-marketing")
    @ResponseStatus(HttpStatus.OK)
    public void updateReceiveMarketing(@RequestParam(name = "agree") final boolean receiveMarketing,
                                       @AuthenticationPrincipal final CustomUserDetails customUserDetails) {
        userService.updateReceiveMarketing(receiveMarketing, customUserDetails.getUsername());
    }
}
